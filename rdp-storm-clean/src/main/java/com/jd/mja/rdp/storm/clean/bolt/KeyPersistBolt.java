package com.jd.mja.rdp.storm.clean.bolt;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.clean.constant.Constant;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.model.Kpi;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;
import com.jd.mja.rdp.storm.service.CacheService;
import com.jd.mja.rdp.storm.service.impl.DeviceServiceImpl;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduce;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduceFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.shade.org.eclipse.jetty.util.StringUtil;
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

import java.util.*;

import static com.jd.mja.rdp.storm.clean.constant.Constant.BEGIN_BIZ;
import static com.jd.mja.rdp.storm.constant.CommonConstant.COMMON_SPLIT;


/**
 * Created by liangchaolei on 2016/9/19.
 */
public class KeyPersistBolt extends BaseRichBolt{
    private static Logger log = LoggerFactory.getLogger(KeyPersistBolt.class);
    private OutputCollector collector;
    private ApplicationContext cnt;
    private CacheService cacheService;


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
        cnt = new ClassPathXmlApplicationContext("spring/spring-cache.xml");
        cacheService= (CacheService) cnt.getBean("cacheService");
    }

    @Override
    public void execute(Tuple input) {
        Order order= (Order) input.getValueByField(Constant.Field_TargetCal_Order);
        try {
            splitOrder(order);
        } catch (Exception e) {
            log.error("splitOrder.error",e);
        }
        collector.ack(input);
    }

    private void splitOrder(Order order){
        if(order!=null){
            String rKey=order.getBiz()+COMMON_SPLIT+order.getKey();
            if(!cacheService.checkIsSave(rKey)) {
                long time = System.currentTimeMillis();

                String biz = order.getBiz();
                biz = biz.replaceFirst(BEGIN_BIZ, "");
                String app = biz.substring(0, biz.indexOf(COMMON_SPLIT));

                String lastBiz = biz.substring(biz.indexOf(COMMON_SPLIT) + 1);
                String configKey = lastBiz.substring(0, lastBiz.indexOf(COMMON_SPLIT));

                biz = lastBiz.substring(lastBiz.indexOf(COMMON_SPLIT) + 1);

                String key = order.getKey();
                Set<String> uuids = new HashSet<String>();
                if (StringUtils.isNotBlank(order.getUuid())) uuids.add("UN");
                if (order.getUuids() != null) {
                    for (Uuid uuid : order.getUuids()) {
                        uuids.add(uuid.getName());
                    }
                }
                Set<String> kpis = new HashSet<String>();
                if (order.getKpis() != null) {
                    for (Kpi kpi : order.getKpis()) {
                        kpis.add(kpi.getKpi() + (kpi.isUseCount() ? "|C" : "|A"));
                    }
                }
                cacheService.saveBizMessage(app, configKey, biz, key, uuids, kpis);
                //不设失效时间
                cacheService.saveValue(rKey,"1",-1);
                log.info("saveBizMessage.Time.cost:{}", System.currentTimeMillis() - time);
            }else{
                log.info("order had record : {}",rKey);
            }
        }else{
            log.info("order is null");
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
