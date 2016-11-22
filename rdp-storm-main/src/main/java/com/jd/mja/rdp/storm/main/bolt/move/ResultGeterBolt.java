package com.jd.mja.rdp.storm.main.bolt.move;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Count;
import com.jd.mja.rdp.storm.service.CacheResDataService;
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
import java.util.Set;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/14.
 */
public class ResultGeterBolt extends BaseRichBolt {

    private static Logger log = LoggerFactory.getLogger(ResultGeterBolt.class);
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
         Map<String,String> keyMap= (Map<String, String>) tuple.getValueByField(Field_resultKey_keyMap);

        if(keyMap!=null){
            for (Map.Entry<String,String> e:keyMap.entrySet()) {
                String key=e.getKey();
                String timeType=e.getValue();
                String value = cacheResDataService.getValue(key);
                Count count=new Count(key,value);
                String res=JSONObject.toJSONString(count);
                log.info("toHbase: "+timeType+"   "+res);
                emit(tuple,new Values(timeType,count));
            }
        }

        collector.ack(tuple);
    }


    protected void emit(Tuple tuple,Values val){
        collector.emit(tuple,val);
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_cacheGet_timeType,Field_cacheGet_value));
    }

}
