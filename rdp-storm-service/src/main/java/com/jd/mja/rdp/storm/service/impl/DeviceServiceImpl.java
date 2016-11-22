package com.jd.mja.rdp.storm.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.constant.CommonConstant.MESSAGE_TYPE;
import com.jd.mja.rdp.storm.dao.mongoDB.MongoDbDao;
import com.jd.mja.rdp.storm.dao.mongoDB.impl.MongoDbDaoImpl;
import com.jd.mja.rdp.storm.dao.redis.impl.DeviceDataDaoImpl;
import com.jd.mja.rdp.storm.service.DeviceService;

import static com.jd.mja.rdp.storm.constant.CommonConstant.*;

/**
 * Created by liangchaolei on 2016/9/20.
 */
public class DeviceServiceImpl implements DeviceService {
    private DeviceDataDaoImpl deviceDataDao;
    private MongoDbDao mongoDbDao;

    public final static String KEY_APPID="appId";
    public final static String KEY_TOKEN="token";
    public final static String KEY_EVENT_LIST="dataQueue";
    public final static String KEY_RES_DEVICE="INIT_DATA";
    public final static String KEY_RES_DATA="QUEUE_DATA";




    @Override
    public void saveOrUpdateDevice(JSONObject json) {
        String appId=json.getString(KEY_APPID);
        String token=json.getString(KEY_TOKEN);

         //存储r2m
        deviceDataDao.set(produceDeviceIDR2mKey(appId,token),json.toString(),EXPIRE_TIME_DEVICE_KEY);
        //存储mongDb
        mongoDbDao.saveDevice(produceDeviceID(appId,token),json);
    }

    @Override
    public JSONObject[] bulidNewJson(MESSAGE_TYPE sourceComponent, JSONObject json) {


        String appId=json.getString(KEY_APPID);
        String token=json.getString(KEY_TOKEN);

        String cache=deviceDataDao.get(produceDeviceIDR2mKey(appId,token));
        JSONObject jsonDevice;
        if(cache!=null && !cache.trim().equals("")){
            jsonDevice=JSONObject.parseObject(cache);
        }else{
            jsonDevice=mongoDbDao.queryDeviceId(produceDeviceID(appId,token));
            if(jsonDevice!=null)
                deviceDataDao.set(produceDeviceIDR2mKey(appId,token),jsonDevice.toString(),EXPIRE_TIME_DEVICE_KEY);
        }

        JSONObject[] jsons = changeJson(sourceComponent, json,jsonDevice);

        return jsons;
    }

    private JSONObject[] changeJson(MESSAGE_TYPE sourceComponent, JSONObject json, JSONObject jsonDevice){
        if(json==null) return null;
        json.put(KEY_RES_DEVICE,jsonDevice);
        if(MESSAGE_TYPE.EVENT.equals(sourceComponent)){
            JSONArray jsonArray=json.getJSONArray(KEY_EVENT_LIST);
            if(jsonArray!=null && jsonArray.size()>0){
                JSONObject[] jsons=new JSONObject[jsonArray.size()];
                for (int i = 0; i <jsonArray.size() ; i++) {
                    JSONObject jsonClone= (JSONObject) json.clone();
                    jsonClone.remove(KEY_EVENT_LIST);
                    JSONObject split = jsonArray.getJSONObject(i);
                    jsonClone.put(KEY_RES_DATA,split);
                    jsons[i]=jsonClone;
                }
                return jsons;
            }else{
                return new JSONObject[]{json};
            }
        }else{
            return new JSONObject[]{json};
        }
    }


    private String produceDeviceIDR2mKey(String appId,String token){
        StringBuilder sb=new StringBuilder(R2M_PREFIX_DEVICE_KEY);
        sb.append(produceDeviceID(appId,token));
        return sb.toString();
    }

    private String produceDeviceID(String appId,String token){
        StringBuilder sb=new StringBuilder();
        sb.append(appId);
        sb.append("-").append(token);
        return sb.toString();
    }


    public void setDeviceDataDao(DeviceDataDaoImpl deviceDataDao) {
        this.deviceDataDao = deviceDataDao;
    }

    public MongoDbDao getMongoDbDao() {
        return mongoDbDao;
    }

    public void setMongoDbDao(MongoDbDao mongoDbDao) {
        this.mongoDbDao = mongoDbDao;
    }

 }
