package com.jd.mja.rdp.storm.main.bolt.result;

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
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class GetValueBolt   extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(GetValueBolt.class);
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


        String biz=tuple.getStringByField(Field_sourceKey_biz);
        String key=tuple.getStringByField(Field_sourceKey_key);
        String kpi=tuple.getStringByField(Field_sourceKey_kpi);

        String value=cacheDataService.getPullData(biz,key,kpi);
        long v=0;
        if(!StringUtils.isEmpty(value)){
            v=Long.parseLong(value);
            if(v>0) {
                log.info("GetValueBolt" + biz + "" + key + "" + kpi + "" + v);

                collector.emit(new Values(biz, key, kpi, v));
            }else{
                log.info("GetValueBolt.0"  +  biz+"   "+key+"   "+kpi+"   null" );

            }
        }else{
            log.info("GetValueBolt"  +  biz+"   "+key+"   "+kpi+"   null" );

        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_getValue_biz,Field_getValue_key,Field_getValue_kpi,Field_getValue_value));
    }
}
