package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.model.Kpi;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;
import com.jd.mja.rdp.storm.util.CalculateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class OperateJsonImpl implements OperateJson{
    private static final String PRE_ACCOUNT ="A" ;
    private String SPLIT= CommonConstant.COMMON_SPLIT;
    private String ADD_SPLIT="\\|";
    private String PRE_KEY="$$";
    private String PRE_ARRAY="##";
    private String LONG_KEY_SPLIT=",";
    private String BIZ_FLAG="@@biz";
    private String KEY_FLAG="@@key";

    private static final Logger logger = LoggerFactory.getLogger(OperateJsonImpl.class);

    private CrossedConn crossedConn;
    public OperateJsonImpl() {
        crossedConn=new CrossedConn(SPLIT);
    }
    public OperateJsonImpl(String SPLIT) {
        this.SPLIT = SPLIT;
        crossedConn=new CrossedConn(SPLIT);
    }
    @Override
    public boolean filter(List<KeyFilter> list, JSONObject json){
        if(list==null || list.size()==0) return true;
        boolean pass=true;
        for (KeyFilter kf:list) {
             String realValue=getFromJsonOfLongKeyOfOne(kf.getKey(),json);
             String compareValue=getFromJsonOfLongKeyOfOne(kf.getValue(),json);
             pass=pass && kf.compare(realValue,compareValue);
        }
        return pass;
    }

    @Override
    public OrderTemp simpleOperate(KeyConfig kc, JSONObject json) {
        String biz=this.getFromJsonOfOne(kc.getBiz(),json);
        String key=this.getFromJsonOfOne(kc.getKey(),json);
        String amount=this.getFromJsonOfLongKeyOfOne(kc.getAmount(),json);
        String count=this.getFromJsonOfLongKeyOfOne(kc.getCount(),json);
        //配置简化，所以以上list只会出现 size =1
        //获取uuid与KPI
        Set<Uuid> uuidModels=this.getUuidModels(kc.getUuids(),json);
        Set<KpiModel> kpiModels=this.getKpiModels(kc.getKpis(),json);
        return new OrderTemp(biz,key,amount,count,uuidModels,kpiModels);
    }

    @Override
    public List<Order> operateArray(String arrayKey, JSONObject json, OrderTemp temp){
        List<Order> pos = new ArrayList<Order>();
        boolean useArray=false;
        // 获取集合
        if(arrayKey!=null && arrayKey.startsWith(PRE_KEY)) {
            List<JSONObject> arrasy = JsonUtil.getArray(arrayKey.substring(PRE_KEY.length()), json);
            //获取集合的值
            if (arrasy != null && arrasy.size() > 0) {
                useArray=true;
                for (JSONObject j : arrasy) {
                    Order kv = new Order();
                    kv.setBiz(getArrayValue(temp.biz, j));
                    kv.setKey(getArrayValue(temp.key, j));
                    kv.setCount(CalculateUtil.getInstance().calInt(getArrayValue(temp.count, j)));
                    kv.setAmount(CalculateUtil.getInstance().calDouble(getArrayValue(temp.amount, j)));
                    updateUuidModel(temp.uuidModels, j);
                    updateKpiModel(temp.kpiModels, j);
                    pos.add(kv);
                }
            }
        }
        if(!useArray) {
            Order kv = new Order();
            kv.setBiz(temp.biz);
            kv.setKey(temp.key);
            kv.setCount(CalculateUtil.getInstance().calInt(temp.count));
            kv.setAmount(CalculateUtil.getInstance().calDouble(temp.amount));
            pos.add(kv);
        }


        //整理uuid,kpi
        for (Order kv:pos) {
            if(temp.uuidModels!=null){
                for (Uuid uuid:temp.uuidModels) {
                    uuid.setValue(uuid.getValue().replace(BIZ_FLAG,kv.getBiz()).replace(KEY_FLAG,kv.getKey()));
                    kv.addUuid(uuid);
                }
            }
            if(temp.kpiModels!=null){
                for (KpiModel kpi:temp.kpiModels)
                    kv.addKpi(kpi.isUseCount()?
                            new Kpi(kpi.getKpi(),CalculateUtil.getInstance().calInt(kpi.getCount())):
                            new Kpi(kpi.getKpi(),CalculateUtil.getInstance().calDouble(kpi.getAmount())));

            }
        }
        return pos;
    }


    //uuid从array获取值
    private void updateUuidModel(Set<Uuid> uuidModels, JSONObject j) {
        try {
            if(uuidModels!=null){
                for (Uuid m:uuidModels) {
                    m.setValue(getArrayValue(m.getValue(),j));
                }
            }
        } catch (Exception e) {
            logger.error("updateUuidModel.error",e);
        }
    }
    //kpi从array获取值
    private void updateKpiModel(Set<KpiModel> models, JSONObject j) {
        try {
            if(models!=null){
                for (KpiModel m:models) {
                    if(m.isUseCount())
                        m.setCount(getArrayValue(m.getCount(),j));
                    else
                        m.setAmount(getArrayValue(m.getCount(),j));

                }
            }
        } catch (Exception e) {
            logger.error("updateKpiModel.error",e);
        }
    }

    /**
     * json={"KEY":"1234"}
     * if key = KEY  return KEY
     * if key = $$KEY  return 1234
     */
    private String getFromJsonOfOne(String key, JSONObject json) {
        //string从json获取值
        return ProduceUtil.onlyOne(getFromJson(key,json));
    }
    /**
     * json={"KEY":"1234"}
     * if key1 = KEY
     *    key2 = $$KEY
     *    return KEY,1234
     */
    private String getFromJsonOfOne(List<String> keys, JSONObject json) {
        //string从json获取值
        return ProduceUtil.onlyOne(getFromJson(keys,json));
    }

    //从list获取值
    private String getArrayValue(String r,JSONObject json){
        return r.indexOf(PRE_ARRAY)==-1?r:getFromJsonOfOne(PRE_KEY+r.substring(PRE_ARRAY.length()), json);
    }


    public Set<Uuid> getUuidModels(List<String> uuids, JSONObject json) {
        try {
            Set<Uuid> uuidModels =null;
            if(uuids!=null && uuids.size()>0) {
                uuidModels = new HashSet<Uuid>();
                for (String uuid:uuids){
                    String[] ss = uuid.split(ADD_SPLIT);
                    if(ss!=null && ss.length > 1 ){
                        Uuid u=new Uuid();
                        String name=ss[0],value=ss[1];
                        u.setName(name);
                        String v=getFromJsonOfLongKeyOfOne(value,json);
                        if(v==null) continue;
                        u.setValue(v);
                        uuidModels.add(u);
                    }
                }
            }
            return uuidModels;
        } catch (Exception e) {
            logger.error("getUuidModels.error",e);
            return null;
        }
    }

    public Set<KpiModel> getKpiModels(List<String> kpis, JSONObject json) {
        try {
            Set<KpiModel> models=null;
            if(kpis!=null && kpis.size()>0) {
                models = new HashSet<KpiModel>();
                for (String uuid:kpis){
                    String[] ss = uuid.split(ADD_SPLIT);
                    if(ss!=null && ss.length > 2 ){
                        KpiModel u=new KpiModel();
                        String name=ss[0],t=ss[1],value=ss[2];
                        u.setKpi(name);
                        if(PRE_ACCOUNT.equals(t)){
                            u.setUseCount(false);
                            String v=getFromJsonOfLongKeyOfOne(value,json);
                            if(v==null) continue;
                            u.setAmount(v);
                        }else {
                            u.setUseCount(true);
                            String v=getFromJsonOfLongKeyOfOne(value,json);
                            if(v==null) continue;
                            u.setCount(v);
                        }
                        models.add(u);
                    }
                }
            }
            return models;
        } catch (Exception e) {
            logger.error("getKpiModels.error",e);
            return null;
        }
    }




    private List<String> getFromJson(String key, JSONObject json) {
        //string从json获取值
        List<String> res=new ArrayList<String>();
        return get(res,json,key);
    }

    //list从json获取值
    private List<String> getFromJson(List<String> list,JSONObject json) {
        List<String> res=new ArrayList<String>();
        for (String  key:list)
            res=get(res,json,key);
        return res;
    }

    private List<String>  get(List<String> res,JSONObject json,String key){
        return  key.startsWith(PRE_KEY) ?
                crossedConn.excute(res,JsonUtil.getValue(json,key.substring(PRE_KEY.length()))) :
                crossedConn.excute(res,key);

    }


    //多段key   key1,key2形式查询
    private String getFromJsonOfLongKeyOfOne(String s, JSONObject json) {
        String[] ss=s.split(LONG_KEY_SPLIT);
        String res="";
        for (String a:ss) {
            res+=LONG_KEY_SPLIT+getFromJsonOfOne(a,json);
        }
        return StringUtils.isBlank(res)?null:res.substring(LONG_KEY_SPLIT.length());
    }



}
