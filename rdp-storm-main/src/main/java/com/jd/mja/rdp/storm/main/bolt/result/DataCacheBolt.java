package com.jd.mja.rdp.storm.main.bolt.result;

import com.jd.mja.rdp.storm.service.CacheDataService;
import com.jd.mja.rdp.storm.service.CacheResDataService;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import com.jd.mja.rdp.storm.util.KeyOperUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.IWindowedBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public class DataCacheBolt   extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(DataCacheBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheResDataService cacheResDataService;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-CacheResData.xml");
        cacheResDataService= (CacheResDataService) cnt.getBean("cacheResDataService");
    }

    @Override
    public void execute(Tuple tuple) {
        String key=tuple.getStringByField(Field_split_key);
        String timeType=tuple.getStringByField(Field_split_TimeType);
        String time=tuple.getStringByField(Field_split_Time);

        long value=tuple.getLongByField(Field_split_value);
        int expire=tuple.getIntegerByField(Field_split_expire);


        log.info("DataCacheBolt=="+KeyOperUtil.connTime(key,timeType,time)+"   "+value+"   "+expire);

        cacheResDataService.incrBy(KeyOperUtil.connTime(key,timeType,time),value,expire);

        collector.emit(InitTimeUtil.useCache(timeType)?STREAM_dateCache_CACHE:STREAM_dateCache_MEMORY,tuple,new Values(timeType,key,time));
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(STREAM_dateCache_CACHE,new Fields(Field_dataCache_timeType,Field_dataCache_key,Field_dataCache_time));
        outputFieldsDeclarer.declareStream(STREAM_dateCache_MEMORY,new Fields(Field_dataCache_timeType,Field_dataCache_key,Field_dataCache_time));
    }
}