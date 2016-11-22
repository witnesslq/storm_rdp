package com.jd.mja.rdp.storm.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.dao.redis.impl.CacheDaoImpl;
import com.jd.mja.rdp.storm.service.CacheService;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.jd.mja.rdp.storm.constant.CommonConstant.*;

/**
 * Created by liangchaolei on 2016/9/23.
 */
public class CacheServiceImpl implements CacheService {
    private static Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

    private CacheDaoImpl cacheDao;

    //获取配置文件方法重写
    @Override
    public List<KeyConfig> get(CommonConstant.MESSAGE_TYPE type, String key) {
        try {
            String config=cacheDao.get(produceConfigKey(type,key));
            return JSONArray.parseArray(config,KeyConfig.class);
        } catch (Exception e) {
            log.error("CacheServiceImpl.get.error",e);
            return null;
        }
    }

    @Override
    public void save(CommonConstant.MESSAGE_TYPE type, String key,String value) {
        cacheDao.save(produceConfigKey(type,key),value);
    }
    @Override
    public boolean checkIsSave(String key) {
        String repeatKey=R2M_PREFIX_CHACK_REPART+key;
        if(StringUtils.isNotBlank(cacheDao.get(repeatKey))) {
            return true;
        }
        return false;
    }
    @Override
    public boolean saveValue(String key,String value,int sec) {
        cacheDao.save(key,value);
        if(sec>0) cacheDao.expire(key,sec);
        return false;
    }
    @Override
    public void saveBizMessage(String app, String configKey, String biz, String key, Set<String> uuids, Set<String> kpis) {
            cacheDao.sadd(R2M_PREFIX_SDK_APP_SET, app);
            cacheDao.sadd(R2M_PREFIX_SDK_CONFIG_SET + app, configKey);
            cacheDao.sadd(R2M_PREFIX_SDK_BIZ_SET + app + COMMON_SPLIT + configKey, biz);
            if(uuids!=null && uuids.size()>0)
                cacheDao.sadd(R2M_PREFIX_SDK_UUID_SET + app + COMMON_SPLIT + configKey + COMMON_SPLIT + biz, uuids.toArray(new String[uuids.size()]));
            if(kpis!=null && kpis.size()>0)
                cacheDao.sadd(R2M_PREFIX_SDK_KPIS_SET + app + COMMON_SPLIT + configKey + COMMON_SPLIT + biz, kpis.toArray(new String[kpis.size()]));
            if(key!=null)
                cacheDao.sadd(R2M_PREFIX_SDK_KEY_SET + app + COMMON_SPLIT + configKey + COMMON_SPLIT + biz, key);
    }

//
//    @Override
//    public void saveAppBizSet(String appid,String biz) {
//        String key=CommonConstant.R2M_PREFIX_APP_BIZ_SET+appid;
//        cacheDao.hset(appid,biz);
//    }
//    @Override
//    public void saveAppKeySet(String appid,String biz,String key) {
//        cacheDao.hset(appid,biz);
//    }
//    @Override
//    public void saveAppUuidSet(String appid,String biz,String key,String uuid) {
//        cacheDao.save(produceConfigKey(type,key),value);
//    }
//    @Override
//    public void saveAppKpiSet(String appid,String biz,String key,String kpi) {
//        cacheDao.save(produceConfigKey(type,key),value);
//    }

    private String produceConfigKey(CommonConstant.MESSAGE_TYPE type, String key){
        StringBuilder sb=new StringBuilder();
        // 前缀 + D + use + part
        sb.append(CommonConstant.R2M_PREFIX_CONFIG_KEY)
                .append(type.toString()).append("-")
                .append(key);
        return sb.toString();
    }


    public void setCacheDao(CacheDaoImpl cacheDao) {
        this.cacheDao = cacheDao;
    }

    public CacheDaoImpl getCacheDao() {
        return cacheDao;
    }


}
