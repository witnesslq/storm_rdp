package com.jd.mja.rdp.storm.dao.hbase.impl;

import com.jd.mja.rdp.storm.dao.hbase.HBaseDataDao;
import com.jd.mja.rdp.storm.model.Count;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liangchaolei on 2016/9/14.
 */
public class HBaseDataTestDaoImpl implements HBaseDataDao{

    private static Logger log = LoggerFactory.getLogger(HBaseDataTestDaoImpl.class);
    public HBaseDataTestDaoImpl() {

    }

    public HBaseDataTestDaoImpl(int poolMaxSize) {
    }
    @Override
    public void writeHBaseTable(String minuteTableName, List<Count> list) throws IOException {
        log.info("writeHBaseTable============"+minuteTableName);
        for (Count c:list) {
            log.info(c.getKey()+"----------"+c.getTotal());
        }
    }
}
