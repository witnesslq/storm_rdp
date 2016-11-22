package com.jd.mja.rdp.storm.util.keyProdecu;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class KpiModel{
    private String kpi;
    private String count;
    private String amount;
    private boolean useCount=true;

    public boolean isUseCount() {
        return useCount;
    }

    public void setUseCount(boolean useCount) {
        this.useCount = useCount;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}