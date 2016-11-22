package com.jd.mja.rdp.storm.main.bolt.result;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.main.constant.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.shade.org.eclipse.jetty.util.ConcurrentHashSet;
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
import java.util.Set;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class SourceKeyGatherBolt extends BaseRichBolt {

    private static Logger log = LoggerFactory.getLogger(SourceKeyGatherBolt.class);
    private OutputCollector collector;
    private Set<String> sourceKeySet;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;

        sourceKeySet=new ConcurrentHashSet<String>();
        log.info("SourceKeyGatherBolt.prepare.ok");

    }

    @Override
    public void execute(Tuple tuple) {
        if(tuple.getSourceComponent().equals(BOLT_coll)) {
            sourceKeyManager(tuple);
        }else{
            setChangeAndEmit(tuple);
        }
        collector.ack(tuple);
    }

    private Set<String> swapSet() {
        Set<String> free=new ConcurrentHashSet<String>();
        Set<String> temp=sourceKeySet;
        sourceKeySet=free;
        return temp;
    }

    private void setChangeAndEmit(Tuple tuple){
        //交换set
        Set<String> temp = swapSet();
        if(temp==null) return ;

        log.info("SourceKeyGatherBolt.timer.begin,size={}",temp.size());

        for (String s:temp) {
            SourceKeyParam skp=JSONObject.parseObject(s,SourceKeyParam.class);
            collector.emit(new Values(skp.getBiz(),skp.getKey(),skp.getKpi()));
        }
    }

    private void sourceKeyManager(Tuple tuple){
        String biz = tuple.getStringByField(Constant.Field_coll_biz);
        String key = tuple.getStringByField(Constant.Field_coll_key);
        String kpi = tuple.getStringByField(Constant.Field_coll_kpi);
        putSet(biz, key, kpi);
    }


    private void putSet(String biz, String key, String kpi) {
        sourceKeySet.add(JSONObject.toJSONString(new SourceKeyParam(biz,key,kpi)));
     }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_sourceKey_biz,Field_sourceKey_key,Field_sourceKey_kpi));
    }




}

class SourceKeyParam {
    private String biz;
    private String key;
    private String kpi;

    public SourceKeyParam(String biz, String key, String kpi) {
        this.biz = biz;
        this.key = key;
        this.kpi = kpi;
    }

    public SourceKeyParam() {
    }

    public String getBiz() {

        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }
}
