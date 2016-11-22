package com.jd.mja.rdp.storm.dao.hbase.impl;

import com.jd.mja.rdp.storm.dao.hbase.HBaseDataDao;
import com.jd.mja.rdp.storm.model.Count;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liangchaolei on 2016/9/14.
 */
public class HBaseDataDaoImpl implements HBaseDataDao{

    private static HTablePool hTablePool = null;
    private static int POOL_MAX_SIZE = 10;

    public HBaseDataDaoImpl() {
        init();
    }

    public HBaseDataDaoImpl(int poolMaxSize) {
        POOL_MAX_SIZE = poolMaxSize;
        init();
    }

    public void writeHBaseTable(String tableName,List<Count> messages) throws IOException {

        HTableInterface table = this.getTable(tableName);

        byte[] family = "cf".getBytes();
        List<Put> puts = new ArrayList<Put>(messages.size());

        for (Count msg : messages) {
            // row key 由to和时间组成，to在前，这样可以分布到不同机器访问。row key设计暂时没有做过的可以联系HBase组支持
            Put put = buildCountResultPut(family, msg);
            puts.add(put);
        }
        table.setAutoFlush(false);
        try {
            table.put(puts);
            table.flushCommits();
        } finally {
            table.close();
        }
    }


    private Put buildCountResultPut(byte[] family, Count msg) {
        String s = msg.getKey();
        byte[] rowKey = Bytes.toBytes(s);
        Put put = new Put(rowKey);
        put.add(family, "c".getBytes(), Bytes.toBytes(msg.getTotal()));
        return put;
    }

    private void init() {
        //此处从配置文件读取配置信息，配置文件在classpath下的hbase-site.xml。
        Configuration configuration = HBaseConfiguration.create();
        // 注意这个值设置的是每个htable表在pool中的最大值，建议根据并发的线程数进行控制。
        hTablePool = new HTablePool(configuration, POOL_MAX_SIZE);
    }

    /**
     * 返回htablepool连接池中的一个htable
     *
     * @param tableName
     * @return
     */
    private HTableInterface getTable(String tableName) {
        return hTablePool.getTable(tableName);// 如果hTablePool对象已经存在，直接取出一个htable
    }

    private void close() {
        try {
            hTablePool.close();
        } catch (IOException e) {
            e.printStackTrace();// to do log
        }
    }
}
