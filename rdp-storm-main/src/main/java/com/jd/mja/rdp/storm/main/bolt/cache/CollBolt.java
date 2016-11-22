package com.jd.mja.rdp.storm.main.bolt.cache;


import static com.jd.mja.rdp.storm.main.constant.Constant.*;

import com.jd.mja.rdp.storm.service.CacheDataService;
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
 * Created by liangchaolei on 2016/9/12.
 */
public class CollBolt  extends BaseRichBolt {

    private static Logger log = LoggerFactory.getLogger(CollBolt.class);

    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheDataService collService;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-CacheData.xml");
        collService= (CacheDataService) cnt.getBean("cacheDataService");
     }

    @Override
    public void execute(Tuple tuple) {

         String biz=tuple.getStringByField(Field_filter_biz);
         String key=tuple.getStringByField(Field_filter_key);
         String kpi=tuple.getStringByField(Field_filter_kpi);
         long value=tuple.getLongByField(Field_filter_value);

         collService.saveSourceData(biz,key,kpi,value);

         collector.emit(new Values(biz,key,kpi));

         collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_coll_biz,Field_coll_key,Field_coll_kpi));
    }
}
