package com.jd.mja.rdp.storm.service.impl;

import com.jd.mja.rdp.storm.dao.hbase.HBaseDataDao;
import com.jd.mja.rdp.storm.model.Count;
import com.jd.mja.rdp.storm.service.HbaseService;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yfhuxiaofeng
 * Date: 14-8-29
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class HBaseServiceImpl implements HbaseService{
    private static final Logger logger = LoggerFactory.getLogger(HBaseServiceImpl.class);

    private String commonTableName = "psa_info";
    private String minuteTableName = "pay_psa_min";
    private HBaseDataDao hbaseDao;



    @Override
    public void writeResult(String period, List<Count> list) throws IOException {
        if(null != period && period.equalsIgnoreCase(HbaseService.TABLE_TYPE_MINUTES)){
            hbaseDao.writeHBaseTable(minuteTableName,list);
        }else {
            hbaseDao.writeHBaseTable(commonTableName,list);
        }
    }


    public void setCommonTableName(String commonTableName) {
        this.commonTableName = commonTableName;
    }

    public String getMinuteTableName() {
        return minuteTableName;
    }

    public void setMinuteTableName(String minuteTableName) {
        this.minuteTableName = minuteTableName;
    }

    public void setHbaseDao(HBaseDataDao hbaseDao) {
        this.hbaseDao = hbaseDao;
    }


}
