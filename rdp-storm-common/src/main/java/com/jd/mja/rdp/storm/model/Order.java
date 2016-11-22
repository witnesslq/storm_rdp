package com.jd.mja.rdp.storm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/11.
 */
public class Order implements Serializable {
    private String biz;
    private String key;
    //如果故意设置为零，则不统计
    private long count=1;
    //为0则不统计
    private double amount =0;
    //去重，如果为空，则不统计
    private String uuid;


    private Set<Uuid> uuids;
    private Set<Kpi>  kpis;


    public Order() {
    }

    public Order(String biz, String key, long count, double amount, String uuid, Set<Uuid> uuids, Set<Kpi> kpis) {

        this.biz = biz;
        this.key = key;
        this.count = count;
        this.amount = amount;
        this.uuid = uuid;
        this.uuids = uuids;
        this.kpis = kpis;
    }

    //name传一个比较短，且有意义的单词，比如 order
    public Order addUuid(String name,String value){
        if(uuids == null) uuids=new HashSet<Uuid>();
        uuids.add(new Uuid(name,value));
        return this;

    }
    public Order addUuid(Uuid uuid){
        if(uuids == null) uuids=new HashSet<Uuid>();
        uuids.add(uuid);
        return this;

    }
    public Order addKpi(Kpi kpi){
        if(kpis == null) kpis=new HashSet<Kpi>();
        kpis.add(kpi);
        return this;
    }
    //name传一个两位大写字母，比如 MY ，会在后面替代 ,C 或 ,A
    public Order addAmountKpi(String name,double value){
        if(kpis == null) kpis=new HashSet<Kpi>();
        kpis.add(new Kpi(name,value));
        return this;
    }

    public Order addCountKpi(String name,long value){
        if(kpis == null) kpis=new HashSet<Kpi>();
        kpis.add(new Kpi(name,value));
        return this;
    }


    public Order(String biz, String key) {
        this.biz = biz;
        this.key = key;
    }

    public Order(String biz, String key, double amount) {
        this.biz = biz;
        this.key = key;
        this.amount = amount;
    }

    public Order(String biz, String key, long count, double amount) {
        this.biz = biz;
        this.key = key;
        this.count = count;
        this.amount = amount;
    }

    public Order(String biz, String key, String uuid) {
        this.biz = biz;
        this.key = key;
        this.uuid = uuid;
    }

    public Order(String biz, String key, String uuid, double amount) {
        this.biz = biz;
        this.key = key;
        this.uuid = uuid;
        this.amount = amount;
    }

    public Order(String biz, String key, long count, double amount, String uuid) {
        this.biz = biz;
        this.key = key;
        this.count = count;
        this.amount = amount;
        this.uuid = uuid;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Set<Uuid> getUuids() {
        return uuids;
    }

    public void setUuids(Set<Uuid> uuids) {
        this.uuids = uuids;
    }

    public Set<Kpi> getKpis() {
        return kpis;
    }

    public void setKpis(Set<Kpi> kpis) {
        this.kpis = kpis;
    }
}


