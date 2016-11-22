package com.jd.mja.rdp.storm.model;


import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: yfhuxiaofeng
 * Date: 14-8-29
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public class Count implements Serializable {
    private String key;
    private String total="0";

    public Count() {
    }

    public Count(String key, String total) {
        this.key = key;
        this.total = total;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
