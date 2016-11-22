package com.jd.mja.rdp.storm.service.impl;

import com.jd.mja.rdp.storm.service.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lxb on 2015/7/3.
 *
 * gen minute clock
 *
 */
public class TimerServiceImpl implements TimerService {
    private static Logger log = LoggerFactory.getLogger(TimerServiceImpl.class);
    private Queue<Long> _q = new LinkedBlockingQueue<Long>();

    public void gen(){
        Long t=System.currentTimeMillis();
        //转化成整秒
        Long time=t/(10*1000)*(10*1000);
        _q.add(time);
        log.info("generated the time success:"+time);

    }


    @Override
    public Long get() {
        return _q.poll();
    }

}
