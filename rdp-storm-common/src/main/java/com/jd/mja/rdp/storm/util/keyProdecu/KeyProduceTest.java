package com.jd.mja.rdp.storm.util.keyProdecu;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.model.Order;

import java.util.List;
import java.util.Map;

/**
 * Created by liangchaolei on 2016/9/24.
 */
public class KeyProduceTest {

    public static void main(String[] args) {
        String config="[{\n" +
                "\tconfigName : \"HELLO1\",\n" +
                "\tbiz : [\"TEST,01\", \"$$A\"],\n" +
                "\tkey : [\"$$B\", \"$$C\"],\n" +
                "\tamount : \"$$AMOUNT\",\n" +
                "\tcount : \"2\"\n" +
                "}, {\n" +
                "\tconfigName : \"HELLO2\",\n" +
                "\tfilter : [{\n" +
                "\t\t\tkey : \"$$AMOUNT\",\n" +
                "\t\t\tcompare : \">\",\n" +
                "\t\t\ttype:\"double\",\n" +
                "\t\t\tvalue : \"$$E.AMOUNT\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\tbiz : [\"TEST,02\", \"$$A\"],\n" +
                "\tkey : [\"$$B\", \"$$C\", \"$$E.E1\"],\n" +
                "\tamount : \"$$E.AMOUNT,-,$$AMOUNT\",\n" +
                "\tcount : \"1\",\n" +
                "\tuuids : [\"PIN|@@key,$$E.E2\", \"ORDER|@@key,$$E.E3\"],\n" +
                "\tkpis : [\"JE|A|$$E.AMOUNT,+,$$AMOUNT\"]\n" +
                "}\n" +
                "]\n";
        String mes="{\n" +
                "\t\"A\":\"A1\",\n" +
                "\t\"B\":\"B1\",\n" +
                "\t\"C\":\"C1\",\n" +
                "\t\"D\":\"D1\",\n" +
                "\t\"E\":{\n" +
                "\t\t\"E1\":\"E11\",\n" +
                "\t\t\"E2\":\"E21\",\n" +
                "\t\t\"AMOUNT\":\"88.88\",\n" +
                "\t\t\"E3\":\"E31\"\n" +
                "\t},\n" +
                "\t\"AMOUNT\":99.99\n" +
                "}\n";

        List<KeyConfig> list= JSONObject.parseArray(config,KeyConfig.class);
        KeyProduce kep=new KeyProduce(list);
        Map<String, List<Order>> listsss = kep.produce(JSONObject.parseObject(mes));
        System.out.println(listsss);
    }
}
