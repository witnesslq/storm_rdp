package com.jd.mja.rdp.storm.clean.bolt;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.clean.constant.Constant;
import com.jd.mja.rdp.storm.model.Order;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/23.
 */
public class KafkaProducerBolt   extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(KafkaProducerBolt.class);
    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
    }

    @Override
    public void execute(Tuple input) {
        Order order = (Order) input.getValueByField(Constant.Field_TargetCal_Order);
        String result=JSONObject.toJSONString(order);
        log.info("KafkaProducerBolt:"+result);
        collector.emit(input,new Values(result));
        collector.ack(input);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constant.Field_kafka_message));
    }
}
