package com.jd.mja.rdp.storm.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangchaolei on 2016/7/13.
 */
public class CalculateUtil {
    private static CalculateUtil ourInstance = new CalculateUtil();

    public static CalculateUtil getInstance() {
        return ourInstance;
    }
    private ScriptEngine engine;
    private CalculateUtil() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");
    }

    //非精确
    public String cal(String s, int pos) {
        //将为空除数转换成0.001
        try {
            s=s.replace("/0.","QQQ")
               .replace("/0","QQQ001")
               .replace("QQQ","/0.");
            Double result = (Double) engine.eval("(" + s + ")*1.0");
            String ss= String.format("%."+pos+"f", result);
            if(isNumeric(ss)) return ss;
            else return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
    public Long calInt(String s){
        if(s==null) return 0L;
        return Long.parseLong(cal(s.replace(",",""),0));
    }
    public Double calDouble(String s){
        if(s==null) return 0D;
        return Double.parseDouble(cal(s.replace(",",""),2));
    }
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }




    public static void main(String[] args) throws Exception {
        System.out.println(CalculateUtil.getInstance().cal("(2+3)*2",0));
        System.out.println(CalculateUtil.getInstance().cal("(2+3)*2",2));
        System.out.println(CalculateUtil.getInstance().calInt("0"));
    }
}
