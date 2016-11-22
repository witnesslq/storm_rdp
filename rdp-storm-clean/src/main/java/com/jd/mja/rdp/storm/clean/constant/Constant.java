package com.jd.mja.rdp.storm.clean.constant;

/**
 * Created by liangchaolei on 2016/9/19.
 */
public class Constant {
    public final static String BEGIN_BIZ="SDK,";

    public final static String SPOUT_source_init="spout_init";
    public final static String SPOUT_source_event="spout_event";
    public final static String SPOUT_source_status="spout_status";


    public final static String BOLT_PersistDivice="PersistDivice";
    public final static String BOLT_ConnDevice="ConnDevice";
    public final static String BOLT_TargetCal="TargetCal";
    public final static String BOLT_Kafka_produce="KafkaProduce";
    public final static String BOLT_Kafka="Kafka";
    public final static String BOLT_Key_persist="keyPersist";




    public  static Integer SPOUT_NUM_source_init=3;
    public  static Integer SPOUT_NUM_source_event=3;
    public  static Integer SPOUT_NUM_source_status=3;

    public static Integer BOLT_NUM_PersistDivice=3;
    public static Integer BOLT_NUM_ConnDevice=3;
    public static Integer BOLT_NUM_TargetCal=3;
    public static Integer BOLT_NUM_Kafka=3;
    public static Integer BOLT_NUM_Kafka_produce=3;
    public static Integer BOLT_NUM_Key_persist=3;




    public static String Field_source_json="json";

    public static String Field_source_Type="type";

    public static String Field_TargetCal_Order="order";

    public static String Field_kafka_key ="key";
    public static String Field_kafka_message ="message";
}
