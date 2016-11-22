package com.jd.mja.rdp.storm.main.constant;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Order;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by liangchaolei on 2016/9/19.
 */
public class KafkaMessageScheme implements Scheme {

    private static final Charset UTF8_CHARSET;
    public static final String STRING_SCHEME_KEY = "str";

    public KafkaMessageScheme() {
    }

    public List<Object> deserialize(ByteBuffer bytes) {
        String mes=deserializeString(bytes);

        try {
            Order order=JSONObject.parseObject(JSONObject.toJSONString(mes),Order.class);
            return new Values(order);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Json parse Exception"+mes);
        }
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
        return new Fields(Constant.Field_source_order);
    }

    static {
        UTF8_CHARSET = Charset.forName("UTF-8");
    }

}
