package com.jd.mja.rdp.storm.model;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class Kpi{
    private String kpi;
    private long count = 0;
    private double amount = 0;

    private boolean useCount=true;

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
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

    public Kpi(String kpi, Double amount) {
        this.kpi = kpi;
        this.amount = amount;

        useCount=false;
    }

    public Kpi() {
    }

    public Kpi(String kpi, Long count) {

        this.kpi = kpi;
        this.count = count;
        useCount=true;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isUseCount() {
        return useCount;
    }
}
