package com.jd.mja.rdp.storm.clean.spout;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.clean.constant.Constant;
import com.jd.mja.rdp.storm.constant.CommonConstant;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/19.
 */
public class JsonObjectScheme implements Scheme {
    private static Logger log = LoggerFactory.getLogger(JsonObjectScheme.class);
     private static final Charset UTF8_CHARSET;
    private CommonConstant.MESSAGE_TYPE messageType;
    public JsonObjectScheme(CommonConstant.MESSAGE_TYPE messageType) {
        this.messageType=messageType;
    }

    public List<Object> deserialize(ByteBuffer bytes) {
        String mes=deserializeString(bytes);
        log.info("InitKafkaMessageScheme.deserialize ：{}",mes);
        try {
            if(!StringUtils.isEmpty(mes)){
                return new Values(messageType,JSONObject.parseObject(mes));
            }
        } catch (Exception e) {
            log.error("JsonObjectScheme.error",e);
        }
        return null;
    }

    public static String deserializeString(ByteBuffer string) {
        if(string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    public Fields getOutputFields() {
        return new Fields(Constant.Field_source_Type,Constant.Field_source_json);
    }

    static {
        UTF8_CHARSET = Charset.forName("UTF-8");
    }

}
