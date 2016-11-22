package com.jd.mja.rdp.storm.main.constant;

/**
 * Created by liangchaolei on 2016/9/11.
 */
public class Constant {

    public final static String SPOUT_source="source";


    public final static String SPOUT_timer="timer";

    public final static String BOLT_timeChanger="timeChanger";

    public final static String BOLT_filter="filter";
    public final static String BOLT_coll="coll";
    public final static String BOLT_sourceKey="sourceKey";
    public final static String BOLT_getValue="getValue";
    public final static String BOLT_split="split";
    public final static String BOLT_dateCacheSave="dateCacheSave";
    public final static String STREAM_dateCache_CACHE="dateCache_cache";
    public final static String STREAM_dateCache_MEMORY="dateCache_memory";


    public final static String BOLT_resultCacheKey="resultCacheKey";
    public final static String BOLT_resultMemoryKey="resultMemeoryKey";
    public final static String BOLT_resultGeter="resultGeter";
    public final static String BOLT_resultHbaseSave="resultHbaseSave";


    public final static  String Field_source_order="order";

    public final static  String Field_filter_biz="biz";
    public final static  String Field_filter_key="key";
    public final static  String Field_filter_kpi="kpi";
    public final static  String Field_filter_value="value";

    public final static  String Field_coll_biz="biz";
    public final static  String Field_coll_key="key";
    public final static  String Field_coll_kpi="kpi";

    public final static  String Field_timer_time="time";

    public final static  String Field_sourceKey_biz="biz";
    public final static  String Field_sourceKey_key="key";
    public final static  String Field_sourceKey_kpi="kpi";

     public final static  String Field_resultKey_keyMap="keyMap";


    public final static  String Field_getValue_biz="biz";
    public final static  String Field_getValue_key="key";
    public final static  String Field_getValue_kpi="kpi";
    public final static  String Field_getValue_value="value";

    public final static  String Field_split_TimeType="timeType";
    public final static  String Field_split_Time="time";
    public final static  String Field_split_key="key";
    public final static  String Field_split_value="value";
    public final static  String Field_split_expire="expire";

    public final static  String Field_timeChange_type="timeType";
    public final static  String Field_timeChange_time="time";

    public final static  String Field_dataCache_timeType="timeType";
    public final static  String Field_dataCache_time="time";
    public final static  String Field_dataCache_key="key";


    public final static  String Field_cacheGet_timeType="timeType";
    public final static  String Field_cacheGet_value="value";


    //不允许改变，定时任务触发者
    public final static int SPOUT_NUM_timer=1;

    //不允许改变,时间更新器
    public final static int BOLT_NUM_timeChanger=1;


    //数据接收器
    public static Integer SPOUT_NUM_source_DEV=5;
    public static Integer SPOUT_NUM_source_KAFKA=5;
    public static Integer SPOUT_NUM_source_JMQ=5;
    //实时处理
    //过滤器
    public static Integer BOLT_NUM_filter=5;
    //收集器
    public static Integer BOLT_NUM_coll=5;
    //原始key切换器
    public static Integer BOLT_NUM_sourceKey=5;
    //原始数据获取
    public static Integer BOLT_NUM_getValue=5;
    //拆分器
    public static Integer BOLT_NUM_split=5;
      //数据更新
    public static Integer BOLT_NUM_dateCacheSave=5;

    //数据迁移
    //结果数据key切换器
    public static Integer BOLT_NUM_cacherResultKey=5;
    public static Integer BOLT_NUM_memoryResultKey=5;
    //结果数据获取
    public static Integer BOLT_NUM_resultGeter=5;
    //数据转移
    public static Integer BOLT_NUM_resultHbaseSave=5;

}
