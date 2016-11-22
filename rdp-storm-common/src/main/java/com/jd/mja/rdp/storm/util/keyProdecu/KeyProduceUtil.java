package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.mja.rdp.storm.model.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/24.
 */
@Deprecated
public class KeyProduceUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyProduceUtil.class);


    //list从json获取值
    private List<String> get(List<String> list, JSONObject json) {
        return get(list,json,false);
    }
    //list从json获取值
    private List<String> get(List<String> list,JSONObject json,boolean delNull) {
        List<String> res=new ArrayList<String>();
        for (String  b:list) {
            if(b.startsWith("$$")){
                b=b.substring(2);
                res=getKey(res,getValue(json,b));
            }else{
                res=getKey(res,b);
            }
        }
        return res;
    }

    //uuid从array获取值
    private void updateUuidModel(Set<Uuid> uuidModels, JSONObject j) {
        try {
            if(uuidModels!=null){
                for (Uuid m:uuidModels) {
                    m.setValue(LongStringGetArray(m.getValue(),j));
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
                        m.setCount(LongStringGetArray(m.getCount(),j));
                    else
                        m.setAmount(LongStringGetArray(m.getCount(),j));

                }
            }
        } catch (Exception e) {
            logger.error("updateUuidModel.error",e);
        }
    }

    //带逗号分隔字符串从json获取值
    private String LongStringGet(String s, JSONObject json) {
        String[] ss=s.split(",");
        String res="";
        for (String a:ss) {
            res+=","+get(a,json).get(0);
        }
        return StringUtils.isBlank(res)?"":res.substring(1);
    }
    //带逗号分隔字符串从list获取值
    private String LongStringGetArray(String s, JSONObject json) {
        if(json==null || s==null) return null;
        String[] ss=s.split(",");
        String res="";
        for (String a:ss) {
            res+=","+getArrayValue(a,json);
        }
        return StringUtils.isBlank(res)?"":res.substring(1);
    }
    //从list获取值
    private String getArrayValue(String r,JSONObject json){
        if(r.indexOf("##")==-1) return r;
        String p=r;
        while (p.indexOf("##")!=-1) {
            p = p.substring(p.indexOf("##")+2);
            String a = p.substring(0, p.indexOf("##"));
            List<String> list = get("$$" + a, json);
            r=r.replace("##"+a+"##",list.get(0));
            p = p.substring(p.indexOf("##")+2);
        }
        return r;
    }

    //string从json获取值
    private List<String> get(String b,JSONObject json) {
        List<String> res=new ArrayList<String>();
        if(b.startsWith("$$")){
            b=b.substring(2);
            res=getKey(res,getValue(json,b));
        }else{
            res=getKey(res,b);
        }
        return res;
    }
    //string从json获取json
    private List<JSONObject> getArray(String c, JSONObject json){
        if(c==null) return null;
        c=c.substring(2);
        List<JSONObject> jsons=new ArrayList<JSONObject>();
        jsons.add(json);
        String[] bizs = c.split("\\.");
        for (int i=0;i<bizs.length;i++) {
            String b=bizs[i];
            jsons=jsonGetKey(b,jsons);
        }
        return jsons;
    }
    //string从list json获取json
    private List<JSONObject> jsonGetKey(String b, List<JSONObject> jsons) {
        if(b.endsWith("]")){
            String bb=b.substring(0,b.indexOf("["));
            String ns=b.substring(b.indexOf("["),b.indexOf("]")+1);
            if(ns.equals("[]")){
                jsons=getJsonByList(jsons,bb);
            }else{
                String n=ns.substring(b.indexOf("[")+1,b.indexOf("]"));
                jsons=getJsonByList(jsons,bb,Integer.parseInt(n));
            }
        }else{
            jsons=getJsonByObject(jsons,b);
        }
        return jsons;
    }

    private List<String> getKey(List<String> res,List<String> vs){
        if(res==null || res.size()==0){
            res=res==null?new ArrayList<String>():res;
            res.addAll(vs);
            return res;
        }else{
            List<String> r=new ArrayList<String>();
            for(String s:res){
                for(String v:vs){
                    r.add(s+","+v);
                }
            }
            return r;
        }
    }
    private List<String> getKey(List<String> res,String vs){
        List<String> list=new ArrayList<String>();
        list.add(vs);
        return getKey(res,list);
    }


    private List<String> getValue(JSONObject json, String c){
        List<JSONObject> jsons=new ArrayList<JSONObject>();
        jsons.add(json);
        String[] bizs = c.split("\\.");
        for (int i=0;i<bizs.length-1;i++) {
            String b=bizs[i];
            jsons=jsonGetKey(b,jsons);
        }

        List<String> res;
        String b=bizs[bizs.length-1];
        if(b.endsWith("]")){
            String bb=b.substring(0,b.indexOf("["));
            String ns=b.substring(b.indexOf("["),b.indexOf("]")+1);
            if(ns.equals("[]")){
                res=getStringByList(jsons,bb);
            }else{
                String n=ns.substring(b.indexOf("[")+1,b.indexOf("]"));
                res=getStringByList(jsons,bb,Integer.parseInt(n));
            }
        }else{
            res=getStringByObject(jsons,b);
        }
        return res;
    }

    private List<JSONObject> getJsonByObject(List<JSONObject> json, String key){
        List<JSONObject> l=new ArrayList<JSONObject>();
        for (JSONObject j:json) {
            l.add(j.getJSONObject(key));
        }
        return l;
    }

    private List<JSONObject> getJsonByList(List<JSONObject> json, String key, int n){
        List<JSONObject> l=new ArrayList<JSONObject>();
        for (JSONObject j:json) {
            l.add(j.getJSONArray(key).getJSONObject(n));
        }
        return json;
    }

    private List<JSONObject> getJsonByList(List<JSONObject> json, String key){
        List<JSONObject> list=new ArrayList<JSONObject>();
        for (JSONObject j:json
                ) {
            JSONArray ja=j.getJSONArray(key);
            for (int i=0;i<ja.size();i++) {
                list.add(ja.getJSONObject(i));
            }
        }
        return list;
    }

    private List<String> getStringByObject(List<JSONObject> json, String key){
        List<String> list=new ArrayList<String>();
        for (JSONObject j:json ) {
            String s=j.getString(key);
            list.add(keyCheck(s));
        }
        return list;
    }

    private List<String> getStringByList(List<JSONObject> json, String key, int n){
        List<String> list=new ArrayList<String>();
        for (JSONObject j:json) {
            String res=j.getJSONArray(key).getString(n);
            list.add(keyCheck(res));
        }
        return list;
    }

    private String keyCheck(String key){
        return  (StringUtils.isNotBlank(key)?key:"NULL").replace(",","，");
    }

    private List<String> getStringByList(List<JSONObject> json, String key){
        List<String> list=new ArrayList<String>();
        for (JSONObject j:json) {
            JSONArray ja=j.getJSONArray(key);
            for (int i=0;i<ja.size();i++) {
                list.addAll(getStringByList(json,key,i));
            }
        }
        return list;
    }

    public Set<KpiModel> getKpiModels(List<String> kpis,JSONObject json) {
        try {
            Set<KpiModel> models=null;
            if(kpis!=null && kpis.size()>0) {
                models = new HashSet<KpiModel>();
                for (String uuid:kpis){
                    String[] ss = uuid.split("\\|");
                    if(ss!=null && ss.length > 2 ){
                        KpiModel u=new KpiModel();
                        String name=ss[0],t=ss[1],value=ss[2];
                        u.setKpi(name);
                        if("A".equals(t)){
                            u.setUseCount(false);
                            u.setAmount(LongStringGet(value,json));
                        }else {
                            u.setUseCount(true);
                            u.setCount(LongStringGet(value, json));
                        }
                        models.add(u);
                    }
                }
            }
            return models;
        } catch (Exception e) {
            logger.error("getUuidModels.error",e);
            return null;
        }
    }
    public Set<Uuid> getUuidModels(List<String> uuids,JSONObject json) {
        try {
            Set<Uuid> uuidModels =null;
            if(uuids!=null && uuids.size()>0) {
                uuidModels = new HashSet<Uuid>();
                for (String uuid:uuids){
                    String[] ss = uuid.split("\\|");
                    if(ss!=null && ss.length > 1 ){
                        Uuid u=new Uuid();
                        String name=ss[0],value=ss[1];
                        u.setName(name);
                        u.setValue(LongStringGet(value,json));
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



}
