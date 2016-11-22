package com.jd.mja.rdp.storm.main;


import com.jd.mja.rdp.storm.main.bolt.test.CountSentenceBolt;
import com.jd.mja.rdp.storm.main.bolt.test.PrintfSentenceBolt;
import com.jd.mja.rdp.storm.main.bolt.test.SplitSentenceBolt;
import com.jd.mja.rdp.storm.main.spout.TestSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lxb on 2015/7/2.
 * <p>
 * the top N stater
 */
public class BuilderTest {


    private static Logger log = LoggerFactory.getLogger(BuilderTest.class);



    private static String SpoutType;
    static{
        log.info("initialize the topology begin");
        ApplicationContext cnt = new ClassPathXmlApplicationContext("spring/spring-main-config.xml");
        SpoutType = (String) cnt.getBean("spoutType");
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

            // 数据源
            builder.setSpout("frist",new TestSpout(), 1);

            //累加
            builder.setBolt("split", new SplitSentenceBolt(), 3).
               shuffleGrouping("frist");

            //累加
            builder.setBolt("count_num", new CountSentenceBolt(), 3).
                    fieldsGrouping("split",new Fields("wordDec"));
            //打印
            builder.setBolt("printf", new PrintfSentenceBolt(), 1).
                    globalGrouping("count_num");

            Config conf = new Config();


            conf.setDebug(true);
        conf.setNumAckers(1);

        LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("countWord", conf, builder.createTopology());
            Utils.sleep(10000);
            cluster.killTopology("countWord");
            cluster.shutdown();

    }








}
