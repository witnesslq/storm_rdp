package com.jd.mja.rdp.storm.main.bolt.move;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.service.CacheDataService;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import com.jd.mja.rdp.storm.util.KeyOperUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Set;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public abstract class ResultKeyGatherBolt extends BaseRichBolt {

    private static Logger log = LoggerFactory.getLogger(ResultKeyGatherBolt.class);
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        //key降临
        if(tuple.getSourceComponent().equals(BOLT_dateCacheSave)) {
            keyIn(tuple);
        }else{
            keyOut(tuple);
        }
        collector.ack(tuple);
    }

    protected abstract void keyIn(Tuple tuple);
    protected abstract void keyOut(Tuple tuple);

    protected void emit(Tuple tuple,Map<String,String> map){
        collector.emit(tuple,new Values(map));
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_resultKey_keyMap));
    }


}
