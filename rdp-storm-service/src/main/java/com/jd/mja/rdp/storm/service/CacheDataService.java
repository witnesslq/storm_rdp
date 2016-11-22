package com.jd.mja.rdp.storm.service;

import java.util.Map;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public interface CacheDataService {


    void saveSourceData(String biz, String key, String kpi, long value);

    String getPullData(String biz, String key, String kpi);

    Map<String,String> getAllNowTime();


    String getNowTime(String timeType);

    void setNowTime(String timeType, String value);

    void setResultKey(String timeType, Map<String,String> keys, String s);

    Map<String, String> getAllResultKey(String timeType, String use, int part);
}
