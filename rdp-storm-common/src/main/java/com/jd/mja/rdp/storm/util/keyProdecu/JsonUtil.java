package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class JsonUtil {

    private final static String SPLIT="\\.";
    /**
     * 获取路径下的值，如果是集合，则返回list
     * eg:  a.b == 1
     * {
     *     a:{
     *         b:"1"
     *     }
     *
     * }
     */
    public static List<String> getValue(JSONObject json, String path){
        List<JSONObject> jsons=getArray(path,json,1);
        //获取值
        List<String> res;
        String[] rr = path.split(SPLIT);
        String b=rr[rr.length-1];
        if(b.endsWith("]")){
            String bb=b.substring(0,b.indexOf("["));
            String ns=b.substring(b.indexOf("["),b.indexOf("]")+1);
            if(ns.equals("[]")){
                res=getStringFromListAll(jsons,bb);
            }else{
                String n=ns.substring(b.indexOf("[")+1,b.indexOf("]"));
                res=getStringFromList(jsons,bb,Integer.parseInt(n));
            }
        }else{
            res=getStringFromObject(jsons,b);
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println("AAA.BBB".substring("AAA.BBB".lastIndexOf(".")+1));
    }
    public static List<JSONObject> getArray(String path, JSONObject json ) {
        return getArray(path,json,0);
    }
    private static List<JSONObject> getArray(String path, JSONObject json,int lastLength){
        if(path==null || json ==null ) return null;
        List<JSONObject> jsons=new ArrayList<JSONObject>();
        jsons.add(json);
        String[] bizs = path.split(SPLIT);
        for (int i=0;i<bizs.length-lastLength;i++) {
            String b=bizs[i];
            jsons=getJson(jsons,b);
        }
        return jsons;
    }



    //string从list json获取json
    private static List<JSONObject> getJson( List<JSONObject> jsons,String key) {
        List<JSONObject> res;
        //如果是集合
        if(key.endsWith("]")){
            String bb=key.substring(0,key.indexOf("["));
            String ns=key.substring(key.indexOf("["),key.indexOf("]")+1);
            if(ns.equals("[]")){
                res=getJsonFromListAll(jsons,bb);
            }else{
                String n=ns.substring(key.indexOf("[")+1,key.indexOf("]"));
                res=getJsonFromList(jsons,bb,Integer.parseInt(n));
            }
        }else{
            res=getJsonFromObject(jsons,key);
        }
        return res.size()==0?null:res;
    }


    // 取json中 key = key的值
    private static List<JSONObject> getJsonFromObject(List<JSONObject> json, String key){
        if(json==null || json.size()==0) return null;
        List<JSONObject> l=new ArrayList<JSONObject>();
        for (JSONObject j:json) {
            JSONObject res = j.getJSONObject(key);
            if(res!=null)
                l.add(res);
        }
        return l;
    }
    // 取json中 key = key的第n个值 json,json[n].key 是一个JsonArray
    private static List<JSONObject> getJsonFromList(List<JSONObject> json, String key, int n){
        if(json==null || json.size()==0) return null;
        List<JSONObject> l=new ArrayList<JSONObject>();
        for (JSONObject j:json) {
            JSONArray array = j.getJSONArray(key);
            if(array!=null && array.size()>n) {
                l.add(array.getJSONObject(n));
            }
        }
        return json;
    }
    // 取所有json中 key = key的 json ,json[i].key 是一个JsonArray
    private static List<JSONObject> getJsonFromListAll(List<JSONObject> json, String key){
        if(json==null || json.size()==0) return null;
        List<JSONObject> list=new ArrayList<JSONObject>();
        for (JSONObject j:json) {
            JSONArray ja=j.getJSONArray(key);
            if(ja!=null) {
                for (int i = 0; i < ja.size(); i++) {
                    JSONObject res = ja.getJSONObject(i);
                    if (res != null) {
                        list.add(res);
                    }
                }
            }
        }
        return list;
    }



    private static List<String> getStringFromList(List<JSONObject> json, String key, int n){
        List<String> list=new ArrayList<String>();
        if(json==null || json.size()==0) return null;
        for (JSONObject j:json) {
            JSONArray ja = j.getJSONArray(key);
            if(ja!=null && ja.size()> n) {
                String res = ja.getString(n);
                list.add(ProduceUtil.keyReplace(res));
            }
        }
        return list;
    }



    private static List<String> getStringFromListAll(List<JSONObject> json, String key){
        List<String> list=new ArrayList<String>();
        if(json==null || json.size()==0) return null;
        for (JSONObject j:json) {
            JSONArray ja=j.getJSONArray(key);
            if(ja!=null)
            for (int i=0;i<ja.size();i++) {
                String res=ja.getString(i);
                list.add(res);
            }
        }
        return list;
    }

    private static List<String> getStringFromObject(List<JSONObject> json, String key){
        List<String> list=new ArrayList<String>();
        if(json==null || json.size()==0) return null;
        for (JSONObject j:json ) {
            String s=j.getString(key);
            list.add(ProduceUtil.keyReplace(s));
        }
        return list;
    }
}
