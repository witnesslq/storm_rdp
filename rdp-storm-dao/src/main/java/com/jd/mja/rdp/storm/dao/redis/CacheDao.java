package com.jd.mja.rdp.storm.dao.redis;



/**
 * Created by liangchaolei on 2016/9/23.
 * 缓存信息,配置缓存、sql缓存等
 */
public interface CacheDao {
    String get(String s);


    void save(String s, String value);


    void expire(String key, int sec);

    void hset(String s, String key, String value);

    void sadd(String key, String ... value);

    void hget(String s, String value);
}
