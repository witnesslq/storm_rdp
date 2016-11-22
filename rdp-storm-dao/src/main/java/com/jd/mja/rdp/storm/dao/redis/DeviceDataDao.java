package com.jd.mja.rdp.storm.dao.redis;

/**
 * Created by liangchaolei on 2016/9/20.
 * 设备缓存
 */
public interface DeviceDataDao {
    void set(String s, String s1, int expireTime);

    String get(String s);
}
