package com.jd.mja.rdp.storm.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.constant.CommonConstant;

/**
 * Created by liangchaolei on 2016/9/20.
 */
public interface DeviceService {



    public void saveOrUpdateDevice(JSONObject json);


    JSONObject[] bulidNewJson(CommonConstant.MESSAGE_TYPE sourceComponent, JSONObject json);
}
