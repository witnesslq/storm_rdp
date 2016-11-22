package com.jd.mja.rdp.storm.dao.redis;

import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/12.
 * 中间结果缓存
 */
public interface CacheDataDao {

    void incrBy(String key, long value);

    void incrBy(String key, long value, int time);

    void sadd(String key, String... value);

    Map<String, String> getThenClear(String key);

    void expire(String key, int time);

    Map<String, String> hgetAll(String key);

    void hset(String key, String field, String value);

    String hget(String key, String field);

    String getSet(String s, long l);

 }
