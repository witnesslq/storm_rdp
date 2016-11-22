package com.jd.mja.rdp.storm.dao.hbase;

import com.jd.mja.rdp.storm.model.Count;

import java.io.IOException;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/14.
 */
public interface HBaseDataDao {
    public void writeHBaseTable(String minuteTableName, List<Count> list) throws IOException;
}
