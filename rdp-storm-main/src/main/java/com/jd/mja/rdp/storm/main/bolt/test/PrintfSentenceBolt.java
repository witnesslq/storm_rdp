package com.jd.mja.rdp.storm.main.bolt.test;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by liangchaolei on 2016/9/9.
 */
public class PrintfSentenceBolt  extends BaseRichBolt {
    private Map<String,Long> countMap;
    private static Logger log = LoggerFactory.getLogger(CountSentenceBolt.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.countMap = new HashMap<String, Long>();
        this.collector=outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {

        String word=tuple.getStringByField("word");
        Long c=tuple.getLongByField("count");
        countMap.put(word,c);


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //do nothing
    }

    @Override
    public void cleanup() {
        log.info("--- FINAL COUNTS ---");
        List<String> keys = new ArrayList<String>();
        keys.addAll(this.countMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            log.info(key + " : " + this.countMap.get(key));
        }
        log.info("--------------");
    }
}
