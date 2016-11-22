package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.model.Uuid;

import java.util.List;
import java.util.Set;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public interface OperateJson {


    boolean filter(List<KeyFilter> list, JSONObject json);

    OrderTemp simpleOperate(KeyConfig kc, JSONObject json);

    List<Order> operateArray(String arrayKey, JSONObject json, OrderTemp temp);
}
class OrderTemp{
    String biz;
    String key;
    String amount;
    String count;
    //配置简化，所以以上list只会出现 size =1
    //获取uuid与KPI
    Set<Uuid> uuidModels;
    Set<KpiModel> kpiModels;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Set<Uuid> getUuidModels() {
        return uuidModels;
    }

    public void setUuidModels(Set<Uuid> uuidModels) {
        this.uuidModels = uuidModels;
    }

    public Set<KpiModel> getKpiModels() {
        return kpiModels;
    }

    public void setKpiModels(Set<KpiModel> kpiModels) {
        this.kpiModels = kpiModels;
    }

    public OrderTemp() {
    }

    public OrderTemp(String biz, String key, String amount, String count, Set<Uuid> uuidModels, Set<KpiModel> kpiModels) {

        this.biz = biz;
        this.key = key;
        this.amount = amount;
        this.count = count;
        this.uuidModels = uuidModels;
        this.kpiModels = kpiModels;
    }
}