package com.jd.mja.rdp.storm.model;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class Uuid {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Uuid() {
    }

    public Uuid(String name, String value) {

        this.name = name;
        this.value = value;
    }
}

