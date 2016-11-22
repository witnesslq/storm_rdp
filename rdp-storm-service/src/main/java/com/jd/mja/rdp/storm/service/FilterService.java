package com.jd.mja.rdp.storm.service;

import com.jd.mja.rdp.storm.model.Order;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public interface FilterService {
    boolean check(Order order);

    boolean pdCheck(String biz, String uuid);
}
