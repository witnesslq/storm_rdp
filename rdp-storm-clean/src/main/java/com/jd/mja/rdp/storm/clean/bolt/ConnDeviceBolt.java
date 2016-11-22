package com.jd.mja.rdp.storm.clean.bolt;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.clean.constant.Constant;
import com.jd.mja.rdp.storm.constant.CommonConstant.MESSAGE_TYPE;
import com.jd.mja.rdp.storm.service.DeviceService;
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
 * Created by liangchaolei on 2016/9/19.
 */
public class ConnDeviceBolt extends BaseRichBolt{

    private static Logger log = LoggerFactory.getLogger(ConnDeviceBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private DeviceService deviceDataService;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-device.xml");
        deviceDataService = (DeviceService) cnt.getBean("deviceDataService");
    }

    @Override
    public void execute(Tuple input) {
        JSONObject json= (JSONObject) input.getValueByField(Constant.Field_source_json);
        MESSAGE_TYPE type= (MESSAGE_TYPE) input.getValueByField(Constant.Field_source_Type);
        if(json!=null) {
             JSONObject[] jsons = deviceDataService.bulidNewJson(type, json);
            for (int i = 0; i < jsons.length; i++) {
                collector.emit(input,new Values(type,jsons[i]));
            }
        }
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constant.Field_source_Type,Constant.Field_source_json));
    }
}
