package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Kpi;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by liangchaolei on 2016/6/20.
 */
public class KeyProduce {
    private static final Logger logger = LoggerFactory.getLogger(KeyProduce.class);
    private List<KeyConfig> configs;
    protected KeyProduce(List<KeyConfig> configs) {
        this.configs = configs;
    }

    public Map<String,List<Order>> produce(JSONObject json){
        Map<String,List<Order>> map = null;
        try {
            if(configs!=null ){
                map=new HashMap<String, List<Order>>();
                for (KeyConfig kc:configs){
                    List<Order> list = produceOne(kc, json);
                    if(list!=null && list.size()>0)
                        map.put(kc.getConfigName(),list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("配置错误",e);
        }
        return map;
    }


    private List<Order> produceOne(KeyConfig kc,JSONObject json){
        OperateJson operate=new OperateJsonImpl();
        if(!operate.filter(kc.getFilter(),json)){
            logger.info("验证不通过");
            return null;
        }
        //$$
        OrderTemp temp = operate.simpleOperate(kc, json);
        //##
        List<Order> pos =operate.operateArray(kc.getArray(),json,temp);

        return pos;
    }





}
