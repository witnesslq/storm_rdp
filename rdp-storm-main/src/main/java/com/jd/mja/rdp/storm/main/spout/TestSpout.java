package com.jd.mja.rdp.storm.main.spout;



import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/9.
 */
public class TestSpout extends BaseRichSpout {
    private static Logger log = LoggerFactory.getLogger(TestSpout.class);

    private SpoutOutputCollector collector;
    private String[] sentences = {
            "my dog has fleas",
            "i like cold beverages",
            "the dog ate my homework",
            "don't have a cow man",
            "i don't think i like fleas"
    };
    private int index = 0;

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
    }

    public void open(Map config, TopologyContext
            context, SpoutOutputCollector collector) {
        log.error("--------------------------------------------------------nextTuple");

        this.collector = collector;
    }
    public void nextTuple() {
        log.error("--------------------------------------------------------nextTuple");
        this.collector.emit(new Values(sentences[index]),System.currentTimeMillis()+Math.random());
        index++;
        if (index >= sentences.length) {
            index = 0;
        }
        Utils.sleep(1);

     }

    @Override
    public void ack(Object msgId) {
        log.error("TestSpout.success===========================================");
     }

    @Override
    public void fail(Object msgId) {
        log.error("TestSpout.fail===========================================");

     }
}