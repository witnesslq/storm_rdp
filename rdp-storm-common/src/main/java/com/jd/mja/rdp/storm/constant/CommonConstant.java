package com.jd.mja.rdp.storm.constant;

import java.io.Serializable;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class CommonConstant {


    public enum MESSAGE_TYPE implements Serializable {
        INIT,
        EVENT,
        STATUS
    }


    public static final int MAX_DIMENSION =  6;
    public static final String AMOUNT_KPI =  "A";
    public static final String COUNT_KPI =  "C";

    public static final int SET_partition =32;

    public static final String USE_CACHE_1 = "1";
    public static final String USE_CACHE_2 = "2";

    public static final String COMMON_SPLIT = ",";







    //-----数据缓冲r2m---------------------------------------------------------------------------
    //源数据key前缀
    public static final String R2M_PREFIX_SOURCE_KEY="MJA-RDP-SOURCE-KEY-";
    //原始key失效时间，24小时
    public static final int EXPIRE_TIME_SOURCE_KEY = 24*60*60;
    //当前时间前缀
    public static final String R2M_MAP_NOW_TIME="MJA-RDP-NOW-TIME";


    //结果keySet前缀
    public static final String R2M_PREFIX_SET_RESULT_KEY="MJA-RDP-SET-RESULT-KEY-";

    //-----结果r2m------------------------------------------------------------------------------------------------
    //结果key前缀
    public static final String R2M_PREFIX_RESULT_KEY="MJA-RDP-RESULT-KEY-";



    //------结果r2m------------------------------------------------------------------------------------------------
    //设备信息前缀
    public static final String R2M_PREFIX_DEVICE_KEY="MJA-RDP-DEVICE-KEY-";
    //device 失效时间 24个小时
    public static final int EXPIRE_TIME_DEVICE_KEY = 24*60*60;

    //------其他r2m------------------------------------------------------------------------------------------------
    //key配置信息缓存
    public static final String R2M_PREFIX_CONFIG_KEY="MJA-RDP-CONFIG-KEY-";



    public static final String R2M_PREFIX_SDK_APP_SET="MJA-SDK-APP-SET";
    public static final String R2M_PREFIX_SDK_CONFIG_SET="MJA-SDK-CONFIG-SET-";
    public static final String R2M_PREFIX_SDK_BIZ_SET="MJA-SDK-BIZ-SET-";
    public static final String R2M_PREFIX_SDK_KEY_SET="MJA-SDK-KEY-SET-";
    public static final String R2M_PREFIX_SDK_UUID_SET="MJA-SDK-UUID-SET-";
    public static final String R2M_PREFIX_SDK_KPIS_SET="MJA-SDK-KPIS-SET-";
    public static final String R2M_PREFIX_CHACK_REPART="MJA-SDK-BIZKEY-REPART-";



}
