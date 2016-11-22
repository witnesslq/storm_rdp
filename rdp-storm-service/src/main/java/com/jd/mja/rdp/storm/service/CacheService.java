package com.jd.mja.rdp.storm.service;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduceFactory;

import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/23.
 */
public interface CacheService extends KeyProduceFactory.ConfigProduce{

    void save(CommonConstant.MESSAGE_TYPE type, String key, String value);

    boolean checkIsSave(String key);

    boolean saveValue(String key, String value, int sec);

    void saveBizMessage(String app, String configKey, String biz, String key, Set<String> uuids, Set<String> kpis);
}
