package com.jd.mja.rdp.storm.service;

import com.jd.mja.rdp.storm.model.Count;

import java.io.IOException;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/14.
 */
public interface HbaseService {
     String TABLE_TYPE_COMMON="C";
    String TABLE_TYPE_MINUTES="M";

    void writeResult(String period, List<Count> list) throws IOException;
}
