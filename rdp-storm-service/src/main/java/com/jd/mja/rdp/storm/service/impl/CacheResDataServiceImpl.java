package com.jd.mja.rdp.storm.service.impl;

import com.jd.mja.rdp.storm.dao.redis.CacheResDataDao;
import com.jd.mja.rdp.storm.service.CacheResDataService;

import static com.jd.mja.rdp.storm.constant.CommonConstant.R2M_PREFIX_RESULT_KEY;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public class CacheResDataServiceImpl implements CacheResDataService{
    private CacheResDataDao cacheResDataDao;

    public void setCacheResDataDao(CacheResDataDao cacheResDataDao) {
        this.cacheResDataDao = cacheResDataDao;
    }

    public CacheResDataDao getCacheResDataDao() {
        return cacheResDataDao;
    }

    @Override
    public void incrBy(String key, long value, int expire) {
        key=R2M_PREFIX_RESULT_KEY+key;
        long mC = cacheResDataDao.incrBy(key,value);
        if (mC == value && expire > 0) {
            cacheResDataDao.expire(key, expire);
        }
    }

    @Override
    public String getValue(String key) {
        key=R2M_PREFIX_RESULT_KEY+key;
        return cacheResDataDao.get(key);
    }
}
