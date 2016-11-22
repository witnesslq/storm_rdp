package com;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.dao.mongoDB.impl.MongoDbFactory;
import com.jd.mja.rdp.storm.service.CacheService;
import com.jd.mja.rdp.storm.service.DeviceService;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyConfig;
import com.jd.mja.rdp.storm.util.keyProdecu.KeyProduceFactory;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by liangchaolei on 2016/9/21.
 */
public class test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext cnt = new ClassPathXmlApplicationContext("spring/spring-cache.xml");
        CacheService cacheService = (CacheService) cnt.getBean("cacheService");
        String value="[\n" +
                "\t{\n" +
                "\t\tconfigName:\"TEST1\",\n" +
                "\t\tbiz : [\"JR\", \"$$appId\"],\n" +
                "\t\tkey : [\"$$os\", \"$$net\"],\n" +
                "\t\tamount : \"0\",\n" +
                "\t\tcount : \"1\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\tconfigName:\"TEST2\",\n" +
                "\t\tfilter : [{\n" +
                "\t\t\tkey : \"$$QUEUE_DATA.type\",\n" +
                "\t\t\tcompare : \"=\",\n" +
                "\t\t\tvalue : \"PAGE_CLOSE\"\n" +
                "\t\t}],\n" +
                "\t\tbiz : [\"JR\", \"$$appId\"],\n" +
                "\t\tkey : [\"$$os\", \"$$INIT_DATA.brand\"],\n" +
                "\t\tamount : \"0\",\n" +
                "\t\tcount : \"1\",\n" +
                "\t\tuuids : [\"PIN|@@key,$$pin\"],\n" +
                "\t\tkpis : [\"SJ|C|$$QUEUE_DATA.interval\"]\n" +
                "\t}\n" +
                "]\n";
        cacheService.save(CommonConstant.MESSAGE_TYPE.EVENT, KeyProduceFactory.ConfigProduce.SYSTEM_CONFIG_KEY,value);
        List<KeyConfig> res = cacheService.get(CommonConstant.MESSAGE_TYPE.EVENT, KeyProduceFactory.ConfigProduce.SYSTEM_CONFIG_KEY);
        System.out.println(res);
    }
}
