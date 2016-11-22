package com.jd.mja.rdp.storm.service;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public interface CacheResDataService {
    void incrBy(String key, long value, int expire);

    String getValue(String key);
}
