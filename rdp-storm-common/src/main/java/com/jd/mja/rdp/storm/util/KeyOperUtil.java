package com.jd.mja.rdp.storm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public class KeyOperUtil {

    public static String[] split(String key) {
        if(key==null) key="";
        //2016年9月6日09:49:48 修复此问题，不加-1 如果最后为分隔符时将被忽略
        String[] s = key.split(",",-1);
        int nCnt = s.length;
        int nBit = (0xFFFFFFFF >>> (32 - nCnt));

        List<String> list = new ArrayList<String>();
        for (int i = 0; i <= nBit; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nCnt; j++) {
                if ((i << (31 - j)) >> 31 == -1) {
                    sb.append(s[j]);
                    if (j < nCnt)
                        sb.append(",");
                } else {
                    sb.append("*,");
                }
            }
            String ret = sb.toString();
            ret = ret.substring(0, ret.length() - 1);
            list.add(ret);
        }
        return list.toArray(new String[0]);
    }
    /**
     * RS算法hash
     */
    public static int rsHash(String str) {
        int b = 378551;
        int a = 63689;
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * a + str.charAt(i);
            a = a * b;
        }

        return (hash & 0x7FFFFFFF);
    }

    public static String connTime(String key, String timeType, String time) {
        StringBuilder sb=new StringBuilder(key);
        sb.append("|").append(timeType);
        sb.append("|").append(time);
        return sb.toString();
    }



//    public static Map<String,String> produceResultKey(String key, String timeType, String time) {
//        return InitTimeUtil.TIME_ENUM.valueOf(timeType).getProduceKey().produce(key, timeType,time);
//    }
}
