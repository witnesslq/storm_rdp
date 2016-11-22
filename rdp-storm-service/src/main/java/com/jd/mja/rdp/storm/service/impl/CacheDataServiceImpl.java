package com.jd.mja.rdp.storm.service.impl;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.dao.redis.CacheDataDao;
import com.jd.mja.rdp.storm.service.CacheDataService;
import com.jd.mja.rdp.storm.util.KeyOperUtil;

import java.util.Map;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class CacheDataServiceImpl implements CacheDataService {

    private CacheDataDao cacheDataDao;


    public void setCacheDataDao(CacheDataDao cacheDataDao) {
        this.cacheDataDao = cacheDataDao;
    }

    @Override
    public void saveSourceData(String biz, String key, String kpi, long value) {
        cacheDataDao.incrBy(produceKey(biz, key, kpi),value,CommonConstant.EXPIRE_TIME_SOURCE_KEY);
    }

    @Override
    public String getPullData(String biz, String key, String kpi) {
        return cacheDataDao.getSet(produceKey(biz, key, kpi),0L);
    }

    @Override
    public Map<String, String> getAllNowTime() {
        return cacheDataDao.hgetAll(CommonConstant.R2M_MAP_NOW_TIME);
    }



    @Override
    public String getNowTime(String timeType) {
        return cacheDataDao.hget(CommonConstant.R2M_MAP_NOW_TIME,timeType);
    }

    @Override
    public void setNowTime(String timeType,String value) {
         cacheDataDao.hset(CommonConstant.R2M_MAP_NOW_TIME,timeType,value);
    }

    @Override
    public void setResultKey(String timeType, Map<String,String> keys, String s) {
        if(keys!=null){
            for(Map.Entry<String,String> key:keys.entrySet()){
                int part=KeyOperUtil.rsHash(key.getKey())%CommonConstant.SET_partition;

                cacheDataDao.hset(produceResultSetKey(timeType,s,part),key.getKey(),key.getValue());
            }
        }
    }

    @Override
    public Map<String, String> getAllResultKey(String timeType, String use, int part) {
        return cacheDataDao.getThenClear(produceResultSetKey(timeType,use,part));
    }




    private String produceResultSetKey(String timeType,String use,int part){
        StringBuilder sb=new StringBuilder();
        // 前缀 + D + use + part
        sb.append(CommonConstant.R2M_PREFIX_SET_RESULT_KEY)
                .append(timeType).append("-")
                .append(use).append("-")
                .append(part);
        return sb.toString();
    }

    private String produceKey(String biz, String key, String kpi){
        StringBuilder sb=new StringBuilder(CommonConstant.R2M_PREFIX_SOURCE_KEY);

        sb.append(biz).append("|");
        sb.append(key).append("|");
        sb.append(kpi).append("|");

        return sb.toString();
    }
}
