package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.exception.KeyProduceException;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liangchaolei on 2016/6/20.
 */
public class KeyProduceFactory {
    private static KeyProduceFactory instance;
    private static Logger log = LoggerFactory.getLogger(KeyProduceFactory.class);

    public ConfigProduce configProduce;
    public Map<String,KeyProduce> map =new ConcurrentHashMap<String,KeyProduce>();
    public Map<String,Long> mapTime =new ConcurrentHashMap<String,Long>();
    private final static Long TIME=5*60*1000L;


    public static KeyProduce creator(CommonConstant.MESSAGE_TYPE type,String config){
        String key=type.toString()+"-"+config;
        if(instance==null) throw new KeyProduceException("KeyProduceFactory not init");
        Long now=System.currentTimeMillis();
        if(instance.chechIsUpdateKey(key,now)) {
            List<KeyConfig> configs=instance.configProduce.get(type,config);
            if(configs!=null && configs.size()>0) {
                instance.map.put(key, new KeyProduce(configs));
            }
            instance.mapTime.put(key,System.currentTimeMillis());
            log.info("{} KeyProduce creator ",key);
        }
        return instance.map.get(key);
    }

    private boolean chechIsUpdateKey(String key,Long now){
        return this.mapTime.get(key)==null || ( this.mapTime.get(key)!=null && now > this.mapTime.get(key)+TIME);
    }

    public static void init(ConfigProduce produce){
        if(instance==null) {
            instance = new KeyProduceFactory(produce);
        }
    }
    public static void updateProduce(ConfigProduce produce){
        instance = new KeyProduceFactory(produce);
    }
    public static void clear() {
        instance=null;
    }

    private KeyProduceFactory(ConfigProduce produce){
        this.configProduce=produce;
    }

    public interface ConfigProduce{
        String SYSTEM_CONFIG_KEY="SYSTEM";
        List<KeyConfig> get(CommonConstant.MESSAGE_TYPE type,String key);
    }

}

