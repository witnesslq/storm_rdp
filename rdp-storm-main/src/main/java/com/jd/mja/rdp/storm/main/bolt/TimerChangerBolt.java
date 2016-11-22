package com.jd.mja.rdp.storm.main.bolt;


import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.service.CacheDataService;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
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


/**
 * Created by liangchaolei on 2016/9/13.
 */
public class TimerChangerBolt    extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(TimerChangerBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheDataService cacheDataService;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-CacheData.xml");
        cacheDataService= (CacheDataService) cnt.getBean("cacheDataService");
    }

    @Override
    public void execute(Tuple tuple) {
        long time=tuple.getLongByField(Constant.Field_timer_time);

        Map<String,String> map=InitTimeUtil.initTime(time);
        for(Map.Entry<String,String> e: map.entrySet()){
            String nowValue = cacheDataService.getNowTime(e.getKey());
            //当时间改变时触发
            if( nowValue==null || !nowValue.equals(e.getValue()) ){
                cacheDataService.setNowTime(e.getKey(),e.getValue());
                log.error("TimerChangerBolt.emit==>>{}:{}",e.getKey(),e.getValue());
                collector.emit(tuple,new Values(e.getKey(),e.getValue()));
            }
        }
        collector.ack(tuple);
    }

    public static void main(String[] args) {
        Map<String,String> map=InitTimeUtil.initTime(System.currentTimeMillis());
        System.out.println(map);

    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Constant.Field_timeChange_type,Constant.Field_timeChange_time));
    }
}
