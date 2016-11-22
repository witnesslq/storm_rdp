package com.jd.mja.rdp.storm.main.bolt.move;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.main.constant.Constant;
import com.jd.mja.rdp.storm.service.CacheDataService;
import com.jd.mja.rdp.storm.util.InitTimeUtil.TIME_ENUM;
import com.jd.mja.rdp.storm.util.InitTimeUtil;
import com.jd.mja.rdp.storm.util.KeyOperUtil;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static com.jd.mja.rdp.storm.main.constant.Constant.*;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class ResultKeyCacheGatherBolt extends ResultKeyGatherBolt {

    private static Logger log = LoggerFactory.getLogger(ResultKeyCacheGatherBolt.class);

    private static Map<String,String> useCache;

    private CacheDataService cacheDataService;
    private ClassPathXmlApplicationContext cnt;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map,topologyContext,outputCollector);
        //初始化呢set内存空间
        useCache=InitTimeUtil.initCache();
        cnt = new ClassPathXmlApplicationContext("spring/spring-CacheData.xml");

        cacheDataService= (CacheDataService) cnt.getBean("cacheDataService");
    }

    @Override
    protected void keyIn(Tuple tuple) {
        String key=tuple.getStringByField(Field_dataCache_key);
        String timeType=tuple.getStringByField(Field_dataCache_timeType);
        String time=tuple.getStringByField(Field_dataCache_time);

        TIME_ENUM timeEnum=TIME_ENUM.valueOf(timeType);

        if(timeEnum.isUseCache()){
            Map<String,String> keys = timeEnum.produceKey(key,time);
            if(keys!=null && keys.size()>0)
                cacheDataService.setResultKey(timeType,keys,useCache.get(timeType));
        }
    }


    @Override
    protected void keyOut(Tuple tuple) {
        //时间变更
        String timeType=tuple.getStringByField(Constant.Field_timeChange_type);
        String time=tuple.getStringByField(Constant.Field_timeChange_time);

        log.info("cacheOut==>{}:{}",timeType,time);
        //测试用，加大小时速率  yyyyMMDDhhmm
        if(time.endsWith("6") ||time.endsWith("3") || time.endsWith("0")){
            log.info("setTimeTypeChange,H");
            timeType="H";
        }
        log.info("cacheOut==2>{}:{}",timeType,time);

        //用缓存，切换缓存，取出缓存
        if(InitTimeUtil.useCache(timeType)){

            //切换
            String nowUse=useCache.get(timeType);
            log.info("useCache===========>"+nowUse);
            String tempKey;
            if(CommonConstant.USE_CACHE_1.equals(nowUse)){
                tempKey=CommonConstant.USE_CACHE_1;
                useCache.put(timeType,CommonConstant.USE_CACHE_2);
            }else{
                tempKey=CommonConstant.USE_CACHE_2;
                useCache.put(timeType,CommonConstant.USE_CACHE_1);
             }
            log.info("useCache==========={}:{}",tempKey,useCache.get(timeType));

            //等待20MS,keyIn完成
            Utils.sleep(20);
            //加载
            for (int i=0;i<CommonConstant.SET_partition;i++) {
                Map<String,String> set=cacheDataService.getAllResultKey(timeType,tempKey,i);
                log.info("cacheOut==>partition:{},{},{}",tempKey,i,set.size());
                super.emit(tuple,set);
            }
        }
    }
}
