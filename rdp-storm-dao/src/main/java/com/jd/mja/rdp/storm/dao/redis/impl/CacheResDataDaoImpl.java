package com.jd.mja.rdp.storm.dao.redis.impl;

import com.jd.mja.rdp.storm.dao.redis.CacheResDataDao;
import com.wangyin.rediscluster.client.R2mClusterClient;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public class CacheResDataDaoImpl implements CacheResDataDao{
    private R2mClusterClient cacheResDataR2m;

    public R2mClusterClient getCacheResDataR2m() {
        return cacheResDataR2m;
    }

    public void setCacheResDataR2m(R2mClusterClient cacheResDataR2m) {
        this.cacheResDataR2m = cacheResDataR2m;
    }


    @Override
    public long incrBy(String key, Long value) {
        return cacheResDataR2m.incrBy(key,value);
    }

    @Override
    public void expire(String key, int expire) {
        cacheResDataR2m.expire(key,expire);
    }

    @Override
    public String get(String key) {
        return cacheResDataR2m.get(key);
    }
}
