package com.jd.mja.rdp.storm.util.keyProdecu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class CrossedConn {
    private String split;

    public CrossedConn(String split) {
        this.split = split;
    }
    /**
     * 交叉组合
     *  res:a  b c
     *  vs :d e
     *  return a,d  b,d  c,d   a,e   b,e  c,e
     */
    private List<String> CrossedConnTwo(List<String> res, List<String> vs){
        if((res==null || res.size()==0) && (vs==null || vs.size()==0)){
            return null;
        }else if(res==null || res.size()==0){
            (res=res==null?new ArrayList<String>():res).addAll(vs);
            return res;
        }else if(vs==null || vs.size()==0){
            (vs=vs==null?new ArrayList<String>():vs).addAll(vs);
            return vs;
        }else{
            List<String> r=new ArrayList<String>();
            for(String s:res)
                for(String v:vs)
                    r.add(s+split+v);
            return r;
        }
    }

    /**
     * 交叉组合
     *  res1:a  b c
     *  res2 :d e
     *  return a,d  b,d  c,d   a,e   b,e  c,e
     */
    private List<String> CrossedConnString(List<String> res, final String vs){
        return CrossedConnTwo(res,new ArrayList<String>(){{add(vs);}});
    }

    public  <T> List<String> excute(T ... res){
        List<String> result=null;
        if(res!=null && res.length>0){
            result=new ArrayList<String>();
            for (int i = 0; i <res.length; i++) {
                T t=res[i];
                if(t==null) {
                    continue;
                }

                if(t instanceof List) {
                    result = CrossedConnTwo(result,(List<String>) t);
                }else{
                    result = CrossedConnString(result,t.toString());
                }
            }
        }
        return result;
    }

}
