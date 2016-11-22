package com.jd.mja.rdp.storm.clean;




import com.jd.mja.rdp.storm.clean.bolt.*;
import com.jd.mja.rdp.storm.clean.constant.Constant;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.util.InitProperties;
import com.jd.mja.rdp.storm.clean.spout.JsonObjectScheme;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.*;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.Scheme;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by lxb on 2015/7/2.
 * <p>
 * the top N stater
 */
public class Builder {


    private static Logger log = LoggerFactory.getLogger(Builder.class);



    private static InitProperties initProperties;
    private static SpoutConfig spoutConfInit;
    private static SpoutConfig spoutConfEvent;
    private static SpoutConfig spoutConfStatus;

    private static String BOLT_KAFKA_BROKER;
    private static String BOLT_KAFKA_TOPIC;
    private static String BOLT_KAFKA_BOOTSTRAP  ;

    static{
        log.info("initialize the topology begin");

        initProperties=new InitProperties("prop.main_config");
        initTaskNum();
        spoutConfInit=initKafka("init",new JsonObjectScheme(CommonConstant.MESSAGE_TYPE.INIT));
        spoutConfEvent=initKafka("event",new JsonObjectScheme(CommonConstant.MESSAGE_TYPE.EVENT));
        spoutConfStatus=initKafka("status",new JsonObjectScheme(CommonConstant.MESSAGE_TYPE.STATUS));

        BOLT_KAFKA_BROKER=initProperties.read("kafka.bolt.zk");
        BOLT_KAFKA_TOPIC=initProperties.read("kafka.bolt.topic");
        BOLT_KAFKA_BOOTSTRAP=initProperties.read("kafka.bolt.bootstrap");


        log.info("initialize the topology end");


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


        builder.setSpout(Constant.SPOUT_source_init,new KafkaSpout(spoutConfInit), Constant.SPOUT_NUM_source_init);
        builder.setSpout(Constant.SPOUT_source_event,new KafkaSpout(spoutConfEvent), Constant.SPOUT_NUM_source_event);
        builder.setSpout(Constant.SPOUT_source_status,new KafkaSpout(spoutConfStatus), Constant.SPOUT_NUM_source_status);



        builder.setBolt(Constant.BOLT_PersistDivice,new PersistDeviceBolt(), Constant.BOLT_NUM_PersistDivice)
                .localOrShuffleGrouping(Constant.SPOUT_source_init);


        builder.setBolt(Constant.BOLT_ConnDevice,new ConnDeviceBolt(), Constant.BOLT_NUM_ConnDevice)
                .localOrShuffleGrouping(Constant.SPOUT_source_event )
                .localOrShuffleGrouping(Constant.SPOUT_source_status);



        builder.setBolt(Constant.BOLT_TargetCal,new TargetCalBolt(), Constant.BOLT_NUM_TargetCal)
                .localOrShuffleGrouping(Constant.SPOUT_source_init)
                .localOrShuffleGrouping(Constant.BOLT_ConnDevice);

        builder.setBolt(Constant.BOLT_Key_persist,new KeyPersistBolt(), Constant.BOLT_NUM_Key_persist)
                .localOrShuffleGrouping(Constant.BOLT_TargetCal);

        builder.setBolt(Constant.BOLT_Kafka_produce,new KafkaProducerBolt(), Constant.BOLT_NUM_Kafka_produce)
                .localOrShuffleGrouping(Constant.BOLT_TargetCal);


        Properties map = new Properties();
        // 配置Kafka broker地址
        map.put("metadata.broker.list", BOLT_KAFKA_BROKER);
        map.put("bootstrap.servers", BOLT_KAFKA_BOOTSTRAP);
        // serializer.class为消息的序列化类localhost:9092
        map.put("serializer.class","kafka.serializer.StringEncoder");
        map.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        map.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        builder.setBolt(Constant.BOLT_Kafka,
                new KafkaBolt<String,Integer>()
                        .withTopicSelector(new DefaultTopicSelector(BOLT_KAFKA_TOPIC))
                        .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper(Constant.Field_kafka_key, Constant.Field_kafka_message))
                        .withProducerProperties(map)
                , Constant.BOLT_NUM_Kafka)
                .localOrShuffleGrouping(Constant.BOLT_Kafka_produce);



        Config conf = new Config();

        if (args != null && args.length > 0) {
            conf.setNumWorkers(18);
            conf.setNumAckers(0);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {

            conf.setDebug(true);
            //设置ack
            //conf.setNumAckers(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("testDataClean", conf, builder.createTopology());

        }

    }



    private static SpoutConfig initKafka(String type,Scheme scheme) {

        String zks = initProperties.read("kafka."+type+".zks");
        String topic = initProperties.read("kafka."+type+".topic");


        String zkRoot = initProperties.read("kafka."+type+".zkRoot"); // default zookeeper root configuration for storm
        String id =initProperties.read("kafka."+type+".id");

        BrokerHosts brokerHosts = new ZkHosts(zks);

        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);

        spoutConf.zkServers=new ArrayList<String>(){{
            for (String s:initProperties.read("kafka.zkservers").split(","))
                add(s);
        }};
        spoutConf.zkPort=initProperties.readInteger("kafka.zkport");
        spoutConf.scheme = new SchemeAsMultiScheme(scheme);

        return spoutConf;

    }

    private static void initTaskNum() {

        Constant.SPOUT_NUM_source_init=initProperties.readInteger("spout.num.init");
        Constant.SPOUT_NUM_source_event=initProperties.readInteger("spout.num.event");
        Constant.SPOUT_NUM_source_status=initProperties.readInteger("spout.num.status");

        Constant.BOLT_NUM_PersistDivice=initProperties.readInteger("bolt.num.persistdevice");
        Constant.BOLT_NUM_ConnDevice=initProperties.readInteger("bolt.num.conndevice");
        Constant.BOLT_NUM_TargetCal=initProperties.readInteger("bolt.num.targetcal");
        Constant.BOLT_NUM_Kafka=initProperties.readInteger("bolt.num.kafka");
        Constant.BOLT_NUM_Kafka_produce=initProperties.readInteger("bolt.num.kafkaproduce");
        Constant.BOLT_NUM_Key_persist=initProperties.readInteger("bolt.num.Keypersist");
    }





}
