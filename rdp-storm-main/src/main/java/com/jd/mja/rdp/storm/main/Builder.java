package com.jd.mja.rdp.storm.main;


import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.main.bolt.TimerChangerBolt;
import com.jd.mja.rdp.storm.main.bolt.cache.CollBolt;
import com.jd.mja.rdp.storm.main.bolt.cache.FilterBolt;
import static com.jd.mja.rdp.storm.main.constant.Constant.*;

import com.jd.mja.rdp.storm.main.bolt.move.ResultKeyCacheGatherBolt;
import com.jd.mja.rdp.storm.main.bolt.move.ResultKeyMemoryGatherBolt;
import com.jd.mja.rdp.storm.main.bolt.move.ResultGeterBolt;
import com.jd.mja.rdp.storm.main.bolt.move.ResultSaveHbaseBolt;
import com.jd.mja.rdp.storm.main.bolt.result.*;
import com.jd.mja.rdp.storm.main.constant.KafkaMessageScheme;
import com.jd.mja.rdp.storm.main.spout.*;
import com.jd.mja.rdp.storm.util.InitProperties;
import com.wangyin.rediscluster.provider.ZkProvider;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
/**
 * Created by lxb on 2015/7/2.
 * <p>
 * the top N stater
 */
public class Builder {


    private static Logger log = LoggerFactory.getLogger(Builder.class);


    private static InitProperties initProperties;
    private static String SpoutType;
    private static SpoutConfig spoutConf;

    private static String FLAG_DEV="DEV";
    private static String FLAG_KAFKA="KAFKA";
    private static String FLAG_JMQ="JMQ";

     static{
         log.info("initialize the topology begin");


         initProperties=new InitProperties("prop.main_config");

         SpoutType = initProperties.read("spoutType");
         initTaskNum();
         if(SpoutType.contains(FLAG_KAFKA))
             initKafka();
         if(SpoutType.contains(FLAG_JMQ)){

         }

         log.info("initialize the topology end");

    }

    private static void initKafka() {
        String zks = initProperties.read("kafka.rdpstream.zks");
        String topic = initProperties.read("kafka.rdpstream.topic");
//        /** * offset在zookeeper中保存的路径 * 路径计算方式为：${zkRoot}/${id}/${partitionId} * 必选参数 **/
        String zkRoot = initProperties.read("kafka.rdpstream.zkRoot");// default zookeeper root configuration for storm
//         kafkaSpout保存offset的不同客户端区分标志
//         建议每个拓扑使用固定的，不同的参数，以保证拓扑重新提交之后，可以从上次位置继续读取数据
//          如果两个拓扑公用同一个id，那么可能会被重复读取  如果在拓扑中使用了动态生成的uuid来作为id，
//          那么每次提交的拓扑，都会从队列最开始位置读取数据  必选参数
        String id =initProperties.read("kafka.rdpstream.id");


        BrokerHosts brokerHosts = new ZkHosts(zks);
        spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);

        spoutConf.zkServers=new ArrayList<String>(){{
            for (String s:initProperties.read("kafka.zkservers").split(","))
                add(s);
        }};
        spoutConf.zkPort=initProperties.readInteger("kafka.zkport");

        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
    }


    private static void initTaskNum() {
        SPOUT_NUM_source_DEV= initProperties.readInteger("soputNumSourceDEV");
        SPOUT_NUM_source_JMQ= initProperties.readInteger("soputNumSourceJMQ");
        SPOUT_NUM_source_KAFKA= initProperties.readInteger("soputNumSourceKAFKA");
        BOLT_NUM_filter= initProperties.readInteger("boltNumFilter");
        BOLT_NUM_coll= initProperties.readInteger("boltNumColl");
        BOLT_NUM_sourceKey= initProperties.readInteger("boltNumSourceKey");

        BOLT_NUM_getValue= initProperties.readInteger("boltNumGetValue");
        BOLT_NUM_split= initProperties.readInteger("boltNumSplit");
        BOLT_NUM_dateCacheSave= initProperties.readInteger("boltNumDataCacheSave");

        BOLT_NUM_cacherResultKey= initProperties.readInteger("boltNumCacherResultKey");
        BOLT_NUM_memoryResultKey= initProperties.readInteger("boltNumMemoryResultKey");
        BOLT_NUM_resultGeter= initProperties.readInteger("boltNumResultGeter");
        BOLT_NUM_resultHbaseSave= initProperties.readInteger("boltNumResultHbaseSave");
    }

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        try {


            regTopology(args);

        } catch (Exception e){
            log.error("main.error",e);

        }
    }

    private static void regTopology(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
            TopologyBuilder builder = new TopologyBuilder();
             /**结果**/
            // 数据源
            if(SpoutType.contains(FLAG_DEV))
                builder.setSpout(SPOUT_source+"-"+FLAG_DEV,new SourceTestSpout(), SPOUT_NUM_source_DEV);
            if(SpoutType.contains(FLAG_JMQ))
                builder.setSpout(SPOUT_source+"-"+FLAG_JMQ,new SourceJmqSpout(), SPOUT_NUM_source_JMQ);
            if(SpoutType.contains(FLAG_KAFKA)){
                builder.setSpout(SPOUT_source+"-"+FLAG_KAFKA,new KafkaSpout(spoutConf), SPOUT_NUM_source_KAFKA);
            }


            /**时间**/
            builder.setSpout(SPOUT_timer,new TimerSpout(), SPOUT_NUM_timer);
//
//            //更新当前时间,时间下发器
            builder.setBolt(BOLT_timeChanger,new TimerChangerBolt(),BOLT_NUM_timeChanger)
                    .globalGrouping(SPOUT_timer);

//            // 过滤，本地或随机分组

            /**数据过滤聚集**/
            BoltDeclarer boltFilter = builder.setBolt(BOLT_filter, new FilterBolt(), BOLT_NUM_filter);

            if(SpoutType.contains(FLAG_DEV))
                boltFilter.localOrShuffleGrouping(SPOUT_source+"-"+FLAG_DEV);
            if(SpoutType.contains(FLAG_JMQ))
                boltFilter.localOrShuffleGrouping(SPOUT_source+"-"+FLAG_JMQ);
            if(SpoutType.contains(FLAG_KAFKA))
                boltFilter.localOrShuffleGrouping(SPOUT_source+"-"+FLAG_KAFKA);
//            //缓存 ，field分组
            builder.setBolt(BOLT_coll,new CollBolt(),BOLT_NUM_coll)
                    .fieldsGrouping(BOLT_filter,new Fields(Field_filter_biz,Field_filter_key,Field_filter_kpi));
            /**结果计算**/

 //            //原始key聚合
            builder.setBolt(BOLT_sourceKey,new SourceKeyGatherBolt(),BOLT_NUM_sourceKey)
                    .fieldsGrouping(BOLT_coll,new Fields(Field_coll_biz,Field_coll_key,Field_coll_kpi))
                    .allGrouping(SPOUT_timer);
//            //缓存、加载
            builder.setBolt(BOLT_getValue,new GetValueBolt(),BOLT_NUM_getValue)
                    .localOrShuffleGrouping(BOLT_sourceKey);
            //拆分、连接
            builder.setBolt(BOLT_split,new SplitBolt(),BOLT_NUM_split)
                    .localOrShuffleGrouping(BOLT_getValue)
                    .allGrouping(BOLT_timeChanger);
            //结果数据更新缓存
            builder.setBolt(BOLT_dateCacheSave,new DataCacheBolt(),BOLT_NUM_dateCacheSave)
                    .localOrShuffleGrouping(BOLT_split);

            /**结果转移**/
            //结果数据 key 缓存
            builder.setBolt(BOLT_resultCacheKey,new ResultKeyCacheGatherBolt(),BOLT_NUM_cacherResultKey)
                    .fieldsGrouping(BOLT_dateCacheSave,STREAM_dateCache_CACHE,new Fields(Field_dataCache_timeType))
                    .allGrouping(BOLT_timeChanger);
            //结果数据 key 内存
            builder.setBolt(BOLT_resultMemoryKey,new ResultKeyMemoryGatherBolt(),BOLT_NUM_memoryResultKey)
                    .fieldsGrouping(BOLT_dateCacheSave,STREAM_dateCache_MEMORY,new Fields(Field_dataCache_timeType,Field_dataCache_key))
                    .allGrouping(BOLT_timeChanger);
            //获取值，
            builder.setBolt(BOLT_resultGeter,new ResultGeterBolt(),BOLT_NUM_resultGeter)
                    .localOrShuffleGrouping(BOLT_resultCacheKey)
                    .localOrShuffleGrouping(BOLT_resultMemoryKey);
            //存入hbase
            builder.setBolt(BOLT_resultHbaseSave,new ResultSaveHbaseBolt(),BOLT_NUM_resultHbaseSave)
                    .localOrShuffleGrouping(BOLT_resultGeter)
                    .allGrouping(SPOUT_timer);


            Config conf = new Config();

            if (args != null && args.length > 0) {
                conf.setNumWorkers(30);
                conf.setNumAckers(0);
                StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
            } else {
                conf.setDebug(true);
                //设置ack
                //conf.setNumAckers(1);
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology("testRdp", conf, builder.createTopology());

            }

    }









}
