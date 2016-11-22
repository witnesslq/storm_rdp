package com.jd.mja.rdp.storm.util.keyProdecu;

import com.jd.jsf.gd.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class ProduceUtil {
    public static <E> E onlyOne(List<E> list){
        return list==null|| list.size()==0 ? null :list.get(0);
    }
    private static final Logger logger = LoggerFactory.getLogger(OperateJsonImpl.class);

    //控制转换、英文都好转换
    public static String keyReplace(String key){
        return  (StringUtils.isNotBlank(key)?key:"NULL").replace(",","，");
    }
    public static Long changeToLong(String value){
        try {
            return  Long.parseLong(value);
        } catch (Exception e) {
            logger.error("changeToInt.error",e);
            return 1L;
        }
    }
    public static Double changeToDouble(String value){
        try {
            return  Double.parseDouble(value);
        } catch (Exception e) {
            logger.error("changeToDouble.error",e);
            return 0D;
        }
    }
}
