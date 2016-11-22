package com.jd.mja.rdp.storm.clean.bolt;

 import com.alibaba.fastjson.JSONObject;
 import com.jd.mja.rdp.storm.clean.constant.Constant;
 import com.jd.mja.rdp.storm.constant.CommonConstant;
 import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.service.CacheService;
 import com.jd.mja.rdp.storm.service.impl.DeviceServiceImpl;
 import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduce;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduceFactory;
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

import java.util.List;
import java.util.Map;

 import static com.jd.mja.rdp.storm.clean.constant.Constant.BEGIN_BIZ;
 import static com.jd.mja.rdp.storm.constant.CommonConstant.COMMON_SPLIT;


/**
 * Created by liangchaolei on 2016/9/19.
 */
public class TargetCalBolt extends BaseRichBolt{
    private static Logger log = LoggerFactory.getLogger(TargetCalBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheService cacheService;


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-cache.xml");
        cacheService= (CacheService) cnt.getBean("cacheService");
        KeyProduceFactory.init(cacheService);

    }

    @Override
    public void execute(Tuple input) {
        JSONObject orderJson= (JSONObject) input.getValueByField(Constant.Field_source_json);
        CommonConstant.MESSAGE_TYPE type= (CommonConstant.MESSAGE_TYPE) input.getValueByField(Constant.Field_source_Type);
        //获取系统配置
        emitKey(input,orderJson,type,KeyProduceFactory.ConfigProduce.SYSTEM_CONFIG_KEY);
        //获取自己配置
        emitKey(input,orderJson,type,orderJson.getString(DeviceServiceImpl.KEY_APPID));

        collector.ack(input);
    }

    private void emitKey(Tuple input,JSONObject orderJson,CommonConstant.MESSAGE_TYPE type,String configKey){
        KeyProduce produce = KeyProduceFactory.creator(type,configKey);
        if(produce!=null) {
            Map<String, List<Order>> map = produce.produce(orderJson);
            if (map != null)
                for (Map.Entry<String, List<Order>> en : map.entrySet()) {
                    if (en != null)
                        for (Order order : en.getValue()) {
                            order.setBiz( BEGIN_BIZ+ configKey +COMMON_SPLIT + en.getKey() + COMMON_SPLIT + order.getBiz());
                            collector.emit(input, new Values(order));
                        }
                }
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(Constant.Field_TargetCal_Order));

    }
}
