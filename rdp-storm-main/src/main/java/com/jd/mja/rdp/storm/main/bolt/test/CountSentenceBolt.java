package com.jd.mja.rdp.storm.main.bolt.test;



import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/9.
 */
public class CountSentenceBolt extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(CountSentenceBolt.class);

    private OutputCollector collector;
    private Map<String,Long> countMap;

    public void prepare(Map config, TopologyContext
            context, OutputCollector collector) {
        log.error("##################################SplitSentenceBolt");

        this.collector = collector;
        this.countMap = new HashMap<String, Long>();
    }

    public void execute(Tuple tuple) {

        log.error("##################################SplitSentenceBolt");


        String word = tuple.getStringByField("wordDec");
        Long c=countMap.get(word);

        if(c==null) c=0L;
        c++;
        countMap.put(word,c);

        collector.emit(new Values(word,c));
        collector.ack(tuple);

    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word","count"));
    }
}
