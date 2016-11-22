package com.jd.mja.rdp.storm.main.bolt.result;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class SplitBolt extends BaseRichBolt {
    private static Logger log = LoggerFactory.getLogger(SplitBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheDataService cacheDataService;

    //当前时间
    Map<String,String> nowTime;
    //当前时间失效时间
    Map<String,Integer> nowTimeExpire;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-CacheData.xml");
        cacheDataService= (CacheDataService) cnt.getBean("cacheDataService");

        nowTime= InitTimeUtil.initTime(System.currentTimeMillis());
        nowTimeExpire=InitTimeUtil.initTimeR2mExpire(System.currentTimeMillis());
    }

    @Override
    public void execute(Tuple tuple) {

        if(tuple.getSourceComponent().equals(Constant.BOLT_timeChanger)){
            timeIncre(tuple);
        }else{
            split(tuple);
        }
        collector.ack(tuple);
    }

    private void timeIncre(Tuple tuple){
        String type=tuple.getStringByField(Field_timeChange_type);
        String time=tuple.getStringByField(Field_timeChange_time);
         nowTime.put(type,time);
    }
    private void split(Tuple tuple){
        String biz=tuple.getStringByField(Field_getValue_biz);
        String key=tuple.getStringByField(Field_getValue_key);
        String kpi=tuple.getStringByField(Field_getValue_kpi);
        long value=tuple.getLongByField(Field_getValue_value);

        String[] keys = KeyOperUtil.split(key);
        for (String kk : keys) {
            StringBuilder sb=new StringBuilder();
            sb.append(biz);
            sb.append(",").append(kk);
            sb.append(",").append(kpi);
            String sourceKey=sb.toString();

            for(Map.Entry entry:nowTime.entrySet()){
                Integer expire=nowTimeExpire.get(entry.getKey());
                if(expire==null) expire=0;
                collector.emit(new Values(sourceKey,entry.getKey(),entry.getValue(),value,expire));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_split_key,Field_split_TimeType,Field_split_Time,Field_split_value,Field_split_expire));
    }
}
