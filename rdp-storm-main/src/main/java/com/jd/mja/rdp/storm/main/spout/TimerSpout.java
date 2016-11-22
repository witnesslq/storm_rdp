package com.jd.mja.rdp.storm.main.spout;

import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.service.TimerService;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class TimerSpout extends BaseRichSpout {
    private static Logger log = LoggerFactory.getLogger(TimerSpout.class);
    private SpoutOutputCollector collector;
    private TimerService timerService;
    private ApplicationContext cnt;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector=spoutOutputCollector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-TimerSpout.xml");
        timerService= (TimerService) cnt.getBean("timerService");
    }

    @Override
    public void nextTuple() {
        Long time= timerService.get();
        if (time != null) {
            log.error("TimerSpout.emit:{}", time);
            collector.emit(new Values(time));
        }
        else {
             Utils.sleep(10);
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Constant.Field_timer_time));
    }
}
