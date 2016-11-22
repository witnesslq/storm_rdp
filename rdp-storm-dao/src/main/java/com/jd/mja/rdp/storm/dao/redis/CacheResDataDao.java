package com.jd.mja.rdp.storm.dao.redis;

import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/12.
 * 最终结果缓存
 */
public interface CacheResDataDao {


    long incrBy(String key, Long value);

    void expire(String key, int expire);

    String get(String key);
}
