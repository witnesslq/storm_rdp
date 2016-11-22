package com.jd.mja.rdp.storm.dao.redis.impl;

import com.jd.mja.rdp.storm.dao.redis.CacheDao;
import com.jd.mja.rdp.storm.dao.redis.CacheDataDao;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyConfig;
import com.wangyin.rediscluster.client.R2mClusterClient;

import java.util.List;

/**
 * Created by liangchaolei on 2016/9/23.
 */
public class CacheDaoImpl implements CacheDao {
    private R2mClusterClient cacheR2m;

    public void setCacheR2m(R2mClusterClient cacheR2m) {
        this.cacheR2m = cacheR2m;
    }


    @Override
    public String get(String s) {
        return  cacheR2m.get(s);
    }
    @Override
    public void save(String s,String value) {
        cacheR2m.set(s,value);
    }

    @Override
    public void expire(String key,int sec) {
        cacheR2m.expire(key,sec);
    }
    @Override
    public void hset(String s, String key, String value) {
        cacheR2m.hset(s,key,value);
    }
    @Override
    public void sadd(String key,String ... value) {
        cacheR2m.sadd(key,value);
    }
    @Override
    public void hget(String s, String value) {
        cacheR2m.set(s,value);
    }
}
