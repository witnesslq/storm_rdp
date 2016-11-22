package com.jd.mja.rdp.storm.dao.redis.impl;

import com.jd.mja.rdp.storm.dao.redis.CacheDataDao;
import com.wangyin.rediscluster.client.R2mClusterClient;

import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class CacheDataDaoImpl implements CacheDataDao {
    private R2mClusterClient cacheDataR2m;


    @Override
    public void incrBy(String key,long value){
        cacheDataR2m.incrBy(key,value);
    }
    @Override
    public void incrBy(String key,long value,int time){
        cacheDataR2m.incrBy(key,value);
        if(time>0)  cacheDataR2m.expire(key,time);
    }
    @Override
    public void sadd(String key, String... value){
        cacheDataR2m.sadd(key,value);
    }
    @Override
    public Map<String, String> getThenClear(String key){
        Map<String,String> res=cacheDataR2m.hgetAll(key);
        cacheDataR2m.del(key);
        return res;
    }


    @Override
    public void expire(String key, int time){
        cacheDataR2m.expire(key,time);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return cacheDataR2m.hgetAll(key);
    }
    @Override
    public void hset(String key, String field, String value ){
        cacheDataR2m.hset(key,field,value);
    }
    @Override
    public String hget(String key, String field ){
        return cacheDataR2m.hget(key,field);
    }
    @Override
    public String getSet(String s, long l) {
        return cacheDataR2m.getSet(s,String.valueOf(l));
    }



    public void setCacheDataR2m(R2mClusterClient cacheDataR2m) {
        this.cacheDataR2m=cacheDataR2m;
    }
}
