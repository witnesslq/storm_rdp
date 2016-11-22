package com.jd.mja.rdp.storm.main.spout;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.main.constant.Constant;
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
 * Created by liangchaolei on 2016/9/11.
 */
public class SourceTestSpout extends BaseRichSpout {
    private static Logger log = LoggerFactory.getLogger(SourceTestSpout.class);
    private SpoutOutputCollector collector;
    private int index=0;

    private Order[] sentences;

    int i=0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        this.sentences= new Order[]{
                new Order("SDK,JR,TEST", "IOS,UN", 111.11),
                new Order("SDK,JR,TEST", "ANDROID,UN", 222.22),
                new Order("SDK,JR,TEST", "H5,UN", 333, 33),
                new Order("SDK,JR,TEST", "IOS,CN", 444, 44),
                new Order("SDK,JR,TEST", "ANDROID,CN", 555, 55),
                new Order("SDK,JR,TEST", "H5,CN", 666, 66)
        };

    }

    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(new Order("SDK,JR,TEST", "IOS,UN", 111.11)));
    }

    @Override
    public void nextTuple() {
//         Order order=  JSONObject.parseObject(,Order.class);
//        Order order=new Order("SDK,JR,TEST", i++ + "", i);
         this.collector.emit(new Values(JSONObject.toJSONString(sentences[index])));
        index++;
        if (index >= sentences.length) {
            index = 0;
        }
        Utils.sleep(100);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Constant.Field_source_order));

    }
}
