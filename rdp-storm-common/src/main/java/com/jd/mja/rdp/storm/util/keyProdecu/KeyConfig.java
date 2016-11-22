package com.jd.mja.rdp.storm.util.keyProdecu;

import com.jd.mja.rdp.storm.util.CalculateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liangchaolei on 2016/6/20.
 */

//[
//        {
//            array : "$$eventList[]",
//            biz : ["JR,JDPaySDK", "##eventId##"],
//            key : ["$$sdkVersion", "$$provider", "$$appID", "$$orderSrc"],
//            amount : "0",
//            count : "1",
//            uuids : ["PIN|@@biz|@@key,$$jdPin", "ORDER|@@biz|@@key,$$orderNum"],
//            kpis : ["JE|1|2"]
//        }
//]

public class KeyConfig {

    private String configName;
    private List<KeyFilter> filter;

    private String array;
    private List<String> biz;
    private List<String> key;
    private String amount;
    private String count;
    private List<String> uuids;
    private List<String> kpis;


    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public List<KeyFilter> getFilter() {
        return filter;
    }

    public void setFilter(List<KeyFilter> filter) {
        this.filter = filter;
    }

    public List<String> getKpis() {
        return kpis;
    }

    public void setKpis(List<String> kpis) {
        this.kpis = kpis;
    }


    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }

    public List<String> getBiz() {
        return biz;
    }

    public void setBiz(List<String> biz) {
        this.biz = biz;
    }

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public static void main(String[] args) {
        System.out.println("ab".compareTo("ae"));
    }

}
class KeyFilter {
    private static final Logger logger = LoggerFactory.getLogger(KeyFilter.class);


    private String key;
    private String compare="=";//null,=,>,>=,<,<=,in,include,beginWith,endWith,beginOf,endOf 或者前面带!
    private String type="String";//String,,Int,Long,Double
    private String value="";
    public boolean compare(String realValue,String compareValue){
        try {
            if(compare.trim().startsWith("!")){
                return  !compare(compare.replace("!","").replace("not","").trim(),realValue,compareValue);
            }else {
                return  compare(compare.trim(),realValue,compareValue);
            }
        } catch (Exception e) {
            logger.error("KeyFilter.error",e);
        }
        return false;
    }
    private boolean compare(String compareType,String realValue,String compareValue){
        if("String".equalsIgnoreCase(type)){

            if(realValue==null) realValue="";
            if(compareValue==null) compareValue="";

            if("=".equalsIgnoreCase(compareType)){
                return  compareValue.equals(realValue);
            }
            else if("null".equalsIgnoreCase(compareType)){
                return realValue==null || realValue.equals("") || realValue.equalsIgnoreCase("NULL");
            }
            else if(">".equalsIgnoreCase(compareType)){
                return compareValue.compareTo(realValue)<=0;
            }
            else if("<".equalsIgnoreCase(compareType)){
                return compareValue.compareTo(realValue)>=0;
            }
            else if("in".equalsIgnoreCase(compareType)){
                return compareValue.contains(realValue);
            }
            else if("include".equalsIgnoreCase(compareType)){
                return realValue!=null && realValue.contains(compareValue);
            }
            else if("beginWith".equalsIgnoreCase(compareType)){
                return realValue!=null && realValue.startsWith(compareValue);
            }
            else if("endWith".equalsIgnoreCase(compareType)){
                return realValue!=null && realValue.endsWith(compareValue);
            }
            else if("beginOf".equalsIgnoreCase(compareType)){
                return compareValue.startsWith(realValue);
            }
            else if("endOf".equalsIgnoreCase(compareType)){
                return compareValue.endsWith(realValue);
            }

        }else if("Int".equalsIgnoreCase(type) || "Long".equalsIgnoreCase(type)){
            long cv = CalculateUtil.getInstance().calInt(compareValue);
            long rv = CalculateUtil.getInstance().calInt(realValue);
            if("=".equalsIgnoreCase(compareType)){
                return rv==cv;
            }
            if(">".equalsIgnoreCase(compareType)){
                return rv>cv;
            }
            if("<".equalsIgnoreCase(compareType)){
                return rv<cv;
            }

        }else if("Double".equalsIgnoreCase(type)){
            Double cv = CalculateUtil.getInstance().calDouble(compareValue);
            Double rv = CalculateUtil.getInstance().calDouble(realValue);
            if("=".equalsIgnoreCase(compareType)){
                return rv==cv;
            }
            if(">".equalsIgnoreCase(compareType)){
                return rv>cv;
            }
            if("<".equalsIgnoreCase(compareType)){
                return rv<cv;
            }
        }
        return false;
    }

    public static Logger getLogger() {
        return logger;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}