package com.jd.mja.rdp.storm.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.mja.rdp.storm.constant.CommonConstant.USE_CACHE_1;

/**
 * Created by liangchaolei on 2016/9/13.
 */
public class InitTimeUtil {




    public enum TIME_ENUM{
        //20tian
        D("yyyyMMdd0000", 20 * 24 * 60 * 60,false,false, BaseProduceKey.getInstance()),
        //20周
        W("yyyyMMdd0000", 20 * 7 * 24 * 60 * 60,false,false, BaseProduceKey.getInstance(),WeekTimeOpre.getInstance()),
        //120分钟,分钟数据使用cache缓存key，请勿随意更改其name
        M("yyyyMMddHHmm",2*60*60,true,false, MinProduceKey.getInstance()),
        //48小时
        H("yyyyMMddHH00",2*24*60*60,false,true, HourProduceKey.getInstance()),
        //月，保存365天
        MO("yyyyMM010000",365*24*60*60,false,false, BaseProduceKey.getInstance());

        private String format;
        //0或小于0为 永久，单位秒
        private int r2mExpireTime;
        private TimeOpre timeChange;
        private ProduceKey produceKey;
        //是否使用缓存
        boolean useCache=true;
        boolean useMemory=true;

        TIME_ENUM(String format,int r2mExpireTime,boolean useMemory,boolean useCache){
            this(format,r2mExpireTime,useMemory,useCache,null,null);
        }
        TIME_ENUM(String format,int r2mExpireTime,boolean useMemory,boolean useCache,ProduceKey pk,TimeOpre timeChange){
            this.format=format;
            this.r2mExpireTime=r2mExpireTime;
            this.produceKey=pk;
            this.useMemory=useMemory;

            this.useCache=useCache;
            this.timeChange=timeChange;
        }
        TIME_ENUM(String format,int r2mExpireTime,boolean useMemory,boolean useCache,TimeOpre timeChange ){
            this(format,r2mExpireTime,useMemory,useCache,null,timeChange);
        }
        TIME_ENUM(String format,int r2mExpireTime,boolean useMemory,boolean useCache,ProduceKey pk ){
            this(format,r2mExpireTime,useMemory,useCache,pk,null);
        }

        public Map<String,String> produceKey(String key,String time){
            return this.getProduceKey()==null?null:this.getProduceKey().produce(key,this.getName(),time);
        }

        public String changeTimeToString(Date date){
            Date d;
            if(this.timeChange!=null){
                d=this.timeChange.change(date);
            }else{
                d=date;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(this.getFormat());
            return sdf.format(d);
        }

        public ProduceKey getProduceKey() {
            return produceKey;
        }


        public static void main(String[] args) {
            System.out.println(InitTimeUtil.useMemory("M"));
            System.out.println(InitTimeUtil.useCache("M"));

            System.out.println(InitTimeUtil.useMemory("D"));
            System.out.println(InitTimeUtil.useCache("D"));

            System.out.println(InitTimeUtil.useMemory("H"));
            System.out.println(InitTimeUtil.useCache("H"));

            TIME_ENUM timeEnum=TIME_ENUM.valueOf("H");

            Map<String,String> keys = timeEnum.produceKey("JR","201509090900");

            TIME_ENUM timeEnum1=TIME_ENUM.valueOf("M");

            Map<String,String> keys1 = timeEnum1.produceKey("JR","201509090901");

            System.out.println(keys);
        }

        public String getName() {
            return this.name();
        }

        public boolean isUseCache() {
            return useCache;
        }

        public String getFormat() {
            return format;
        }

        public int getR2mExpireTime() {
            return r2mExpireTime;
        }

        public boolean isUseMemory() {
            return useMemory;
        }
    }

    public static  Map<String,ConcurrentHashMap<String,String>> initMap() {
        Map<String,ConcurrentHashMap<String,String>> map=new ConcurrentHashMap<String, ConcurrentHashMap<String,String>>();
        TIME_ENUM[] enums=TIME_ENUM.values();
        for (TIME_ENUM enmu:enums) {
            //不用缓存，则用内存，创建存放空间
            if(enmu.isUseMemory()){
                ConcurrentHashMap<String,String> set=new ConcurrentHashMap<String,String>();
                map.put(enmu.getName(),set);
            }
        }
        return map;
    }
    public static  Map<String,String> initCache() {
        Map<String,String> map=new ConcurrentHashMap<String, String>();
        TIME_ENUM[] enums=TIME_ENUM.values();
        for (TIME_ENUM enmu:enums) {
            //不用缓存，则用内存，创建存放空间
            if(enmu.isUseCache()){
                map.put(enmu.getName(),USE_CACHE_1);
            }
        }
        return map;
    }

    public static boolean useCache(String timeType) {
        return TIME_ENUM.valueOf(timeType).isUseCache();
    }
    public static boolean useMemory(String timeType) {
        return TIME_ENUM.valueOf(timeType).isUseMemory();
    }

    //初始化时间
    public static Map<String,String> initTime(long timestamp){
        Date date=new Date(timestamp);
        TIME_ENUM[] types=TIME_ENUM.values();
        Map<String,String> map=new ConcurrentHashMap<String, String>();
        for (TIME_ENUM type:types) {
            map.put(type.getName(), type.changeTimeToString(date));
        }
        return map;

    }

    //初始化缓存时间
    public static Map<String,Integer> initTimeR2mExpire(long timestamp){
        TIME_ENUM[] types=TIME_ENUM.values();
        Map<String,Integer> map=new ConcurrentHashMap<String, Integer>();
        for (TIME_ENUM type:types) {
            map.put(type.getName(),type.getR2mExpireTime());
        }
        return map;
    }


}
interface TimeOpre{
    Date change(Date d);
}

class WeekTimeOpre implements TimeOpre {
    private static WeekTimeOpre instance=new WeekTimeOpre();

    private WeekTimeOpre() {
    }
    public static WeekTimeOpre getInstance(){
        return instance;
    }
    @Override
    public Date change(Date d) {
        Calendar cal =Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }
}

interface  ProduceKey{
    Map<String,String> produce(String key,String timeType,String time);
}
class BaseProduceKey implements ProduceKey{
    private static ProduceKey instance=new BaseProduceKey();

    private BaseProduceKey() {
    }
    public static ProduceKey getInstance(){
        return instance;
    }
    @Override
    public Map<String,String> produce(String key, final String timeType, String time) {
//        final String newKey=KeyOperUtil.connTime(key,timeType,time);
        return null;
    }
}
class HourProduceKey implements ProduceKey{
    private static Logger log = LoggerFactory.getLogger(HourProduceKey.class);

    private static ProduceKey instance=new HourProduceKey();

    private HourProduceKey() {
    }
    public static ProduceKey getInstance(){
        return instance;
    }
    //yyyyHHddHH00
    @Override
    public Map<String,String> produce(String key, final String timeType, String time) {
        Map<String,String> map=new HashMap<String,String>();

        String newKey=KeyOperUtil.connTime(key,timeType,time);
        map.put(newKey,timeType);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
        try {
            Date d=sdf.parse(time);
            String newKeyW=KeyOperUtil.connTime(key,InitTimeUtil.TIME_ENUM.W.getName(),InitTimeUtil.TIME_ENUM.W.changeTimeToString(d));
            String newKeyD=KeyOperUtil.connTime(key,InitTimeUtil.TIME_ENUM.D.getName(),InitTimeUtil.TIME_ENUM.D.changeTimeToString(d));
            String newKeyMo=KeyOperUtil.connTime(key,InitTimeUtil.TIME_ENUM.MO.getName(),InitTimeUtil.TIME_ENUM.MO.changeTimeToString(d));

            map.put(newKeyD,InitTimeUtil.TIME_ENUM.D.getName());
            map.put(newKeyW,InitTimeUtil.TIME_ENUM.W.getName());
            map.put(newKeyMo,InitTimeUtil.TIME_ENUM.MO.getName());
        } catch (Exception e) {
            log.error("HourProduceKey.produce",e);
        }

        return map;
    }


}
class MinProduceKey implements ProduceKey{
    private static Logger log = LoggerFactory.getLogger(HourProduceKey.class);

    private static ProduceKey instance=new MinProduceKey();

    private MinProduceKey() {
    }
    public static ProduceKey getInstance(){
        return instance;
    }

    @Override
    public Map<String,String> produce(String key, final String timeType, String time) {

        Map<String,String> map=new HashMap<String,String>();

        String newKey=KeyOperUtil.connTime(key,timeType,time);
        map.put(newKey,timeType);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
        try {
            Date d=sdf.parse(time);
            String newKeyD=KeyOperUtil.connTime(key,InitTimeUtil.TIME_ENUM.D.getName(),InitTimeUtil.TIME_ENUM.D.changeTimeToString(d));
            map.put(newKeyD,InitTimeUtil.TIME_ENUM.D.getName());
        } catch (Exception e) {
            log.error("MinProduceKey.produce",e);
        }

        return map;
    }

}