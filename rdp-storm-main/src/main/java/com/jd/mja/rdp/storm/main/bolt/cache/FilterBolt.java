package com.jd.mja.rdp.storm.main.bolt.cache;

import com.jd.fastjson.JSONObject;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.model.Kpi;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;
import com.jd.mja.rdp.storm.service.FilterService;
import com.jd.mja.rdp.storm.util.CommonMath;
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
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/11.
 */
public class FilterBolt  extends BaseRichBolt {

    private static Logger log = LoggerFactory.getLogger(FilterBolt.class);

    private OutputCollector collector;


    private  FilterService filterService;
    private  ApplicationContext cnt;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        log.info("FilterBolt prepare begin");
        cnt = new ClassPathXmlApplicationContext("spring/spring-FilterBolt.xml");
        filterService = (FilterService) cnt.getBean("filterService");
        log.info("FilterBolt prepare ok");
    }

    @Override
    public void execute(Tuple tuple) {
        String orderString = tuple.getString(0);
        if (orderString==null || StringUtils.isEmpty(orderString.trim())){
                collector.ack(tuple);
                return;
        }
        Order order= null;
        try {
            order = JSONObject.parseObject(orderString,Order.class);
        } catch (Exception e) {
            log.error("parseObject orderString error:"+orderString,e);
            collector.ack(tuple);
            return ;
        }
        //校验
        if(filterService.check(order)){
            //分拆
            emitData(tuple,order,"");
            if(order.getUuid()!=null && !filterService.pdCheck(order.getBiz(),order.getUuid())){
                emitData(tuple,order,"UN,");
            }
            if(order.getUuids() !=null){
                for (Uuid uuid: order.getUuids()) {
                    if(!filterService.pdCheck(order.getBiz()+","+uuid.getName(),uuid.getValue())){
                        emitData(tuple,order,"UN,"+uuid.getName()+",");
                    }
                }
            }
        }

        collector.ack(tuple);

    }

    private void emitData(Tuple tuple,Order order,String bizPre){
        StringBuilder sb=new StringBuilder();
        sb.append(bizPre);
        sb.append(order.getBiz());
        if(order.getCount()!=0){
            collector.emit(tuple,new Values(sb.toString(),order.getKey(), CommonConstant.COUNT_KPI,order.getCount()));
        }
        if(order.getAmount() != 0){
            collector.emit(tuple,new Values(sb.toString(),order.getKey(),CommonConstant.AMOUNT_KPI, CommonMath.double_100(order.getAmount())));
        }
        if(order.getKpis()!=null){
            for (Kpi kpi:order.getKpis()) {
                if(kpi.isUseCount()) {
                    collector.emit(tuple,new Values(sb.toString(), order.getKey(), kpi.getKpi(),kpi.getCount()));
                }else{
                    collector.emit(tuple,new Values(sb.toString(), order.getKey(), kpi.getKpi(), CommonMath.double_100(kpi.getAmount())));
                }
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(Field_filter_biz,Field_filter_key,Field_filter_kpi,Field_filter_value));
    }
}
