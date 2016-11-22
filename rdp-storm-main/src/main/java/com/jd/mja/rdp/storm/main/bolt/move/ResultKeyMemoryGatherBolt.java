package com.jd.mja.rdp.storm.main.bolt.move;

import com.jd.jsf.gd.util.ConcurrentHashSet;

import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import com.jd.mja.rdp.storm.util.KeyOperUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class ResultKeyMemoryGatherBolt extends ResultKeyGatherBolt {

    private static Logger log = LoggerFactory.getLogger(ResultKeyMemoryGatherBolt.class);

    private Map<String,ConcurrentHashMap<String,String>> mapUse;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map,topologyContext,outputCollector);
        //初始化呢set内存空间
        mapUse=InitTimeUtil.initMap();
    }

    @Override
    protected void keyIn(Tuple tuple) {
        String key=tuple.getStringByField(Field_dataCache_key);
        String timeType=tuple.getStringByField(Field_dataCache_timeType);
        String time=tuple.getStringByField(Field_dataCache_time);
        InitTimeUtil.TIME_ENUM timeEnum= InitTimeUtil.TIME_ENUM.valueOf(timeType);
        if(timeEnum.isUseMemory()){
            ConcurrentHashMap<String,String> setUse=mapUse.get(timeType);
            if(setUse!=null){
                Map<String,String> keys = timeEnum.produceKey(key, time);
                if(keys!=null && keys.size()>0)
                    setUse.putAll(keys);
            }else{
                log.info("MemoryInNull,timeType is {} ",timeType);
            }
        }
    }

    @Override
    protected void keyOut(Tuple tuple) {
        //时间变更
        String timeType=tuple.getStringByField(Constant.Field_timeChange_type);
        String time=tuple.getStringByField(Constant.Field_timeChange_time);

        log.info("MemoryOut==>{}:{}",timeType,time);

        //用缓存，切换缓存，取出缓存
        if(InitTimeUtil.useMemory(timeType)){
            //交换
            ConcurrentHashMap<String,String>  free=new ConcurrentHashMap<String,String>();
            ConcurrentHashMap<String,String> temp=mapUse.get(timeType);
            mapUse.put(timeType,free);

            log.info("MemoryOut==>size:{}",temp.size());

            //等待20MS,keyIn完成
            Utils.sleep(20);
            super.emit(tuple,temp);
        }
    }

}
