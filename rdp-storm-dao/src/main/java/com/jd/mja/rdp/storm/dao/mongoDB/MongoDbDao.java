package com.jd.mja.rdp.storm.dao.mongoDB;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by liangchaolei on 2016/9/21.
 */
public interface MongoDbDao {

    void saveDevice(String _id, JSONObject json);

    JSONObject queryDeviceId(String _id);
}
