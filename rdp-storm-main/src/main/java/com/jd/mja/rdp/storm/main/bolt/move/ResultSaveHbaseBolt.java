package com.jd.mja.rdp.storm.main.bolt.move;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.model.Count;
import com.jd.mja.rdp.storm.service.CacheResDataService;
import com.jd.mja.rdp.storm.service.HbaseService;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import com.jd.ump.profiler.CallerInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/14.
 */
public class ResultSaveHbaseBolt  extends BaseRichBolt {

    private static Logger logger = LoggerFactory.getLogger(ResultSaveHbaseBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    //普通结果临时集
    private List<Count> comResList;
    //分钟结果临时集
    private List<Count> minResList;
    private HbaseService hbaseService;

    Boolean isFlushMinHbase=false;
    Boolean isFlushComHbase=false;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;

        cnt = new ClassPathXmlApplicationContext("spring/spring-HbaseService.xml");
        hbaseService=(HbaseService) cnt.getBean("hbaseService");
        comResList=new CopyOnWriteArrayList<Count>();
        minResList=new CopyOnWriteArrayList<Count>();
    }

    @Override
    public void execute(Tuple tuple) {
        if(tuple.getSourceComponent().equals(Constant.SPOUT_timer)){
            logger.info("ResultSaveHbaseBolt.execute...");
            //flush数据
            resetCom();
            resetMin();
        }else {
            String timeType = tuple.getStringByField(Field_cacheGet_timeType);
            Count result = (Count) tuple.getValueByField(Field_cacheGet_value);
            if (null != result) {
                addResList(timeType, result);
                tryFlushHbase();
            }
        }
        collector.ack(tuple);
    }

    private void addResList(String timeType,Count result){
        InitTimeUtil.TIME_ENUM e=InitTimeUtil.TIME_ENUM.valueOf(timeType);
        if(e.equals(InitTimeUtil.TIME_ENUM.M))  minResList.add(result);
        else  comResList.add(result);
    }

    private void tryFlushHbase(){
        logger.info("tryFlushHbase...");
        if(comResList.size() >= 5000){
            resetCom();
        }
        if(minResList.size() >= 5000){
            resetMin();
        }
    }

    private void resetCom(){
        if(isFlushComHbase){
            return;
        }
        logger.info("resetComIng...");
        isFlushComHbase = true;
        try {
            logger.info("resetComBegin...");
            List<Count> free=new CopyOnWriteArrayList<Count>();
            List<Count> temp=comResList;
            comResList=free;
            flushHBase(HbaseService.TABLE_TYPE_COMMON,temp);
        } catch (Exception e) {
            logger.info("resetComError...",e);
        } finally {
            isFlushComHbase=false;
        }
    }
    private void resetMin(){
        if(isFlushMinHbase){
            return;
        }
        logger.info("resetMinIng...");
        isFlushMinHbase = true;
        try {
            List<Count> free=new CopyOnWriteArrayList<Count>();
            List<Count> temp=minResList;
            minResList=free;
            flushHBase(HbaseService.TABLE_TYPE_MINUTES,temp);
        } catch (Exception e) {
            logger.info("resetMinError...",e);
        } finally {
            isFlushMinHbase=false;
        }


    }


    public static void main(String[] args) {
        List<String> l1=new ArrayList<String>();
        l1.add("1111");

        List<String> free=new ArrayList<String>();
        List<String> temp=l1;
        l1=free;

        System.out.println(l1.size());
        System.out.println(temp.size());
    }

    private void flushHBase(String period,List<Count> list) {
        logger.info("begin flushHBase .size()="+list.size() );
        if (list!=null && list.size() > 0) {
            long begin = System.currentTimeMillis();
            try {
                this.hbaseService.writeResult(period,list);
            } catch (Exception e) {
                logger.error("flushHBase.error", e);
            }
            long end = System.currentTimeMillis();
            logger.info("list.size()=" + list.size() + ",wirteHBaseCost" + (end - begin));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

}