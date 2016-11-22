package com.jd.mja.rdp.storm.dao.redis.impl;

import com.jd.mja.rdp.storm.dao.redis.DeviceDataDao;
import com.wangyin.rediscluster.client.R2mClusterClient;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class DeviceDataDaoImpl implements DeviceDataDao {
    private R2mClusterClient deviceDataR2m;

    public void setDeviceDataR2m(R2mClusterClient deviceDataR2m) {
        this.deviceDataR2m = deviceDataR2m;
    }

    public R2mClusterClient getDeviceDataR2m() {
        return deviceDataR2m;
    }

    @Override
    public void set(String s, String s1, int expireTime) {
        deviceDataR2m.set(s,s1);
        if(expireTime>0){
            deviceDataR2m.expire(s,expireTime);
        }
    }

    @Override
    public String get(String s) {
        return deviceDataR2m.get(s);
    }
}
