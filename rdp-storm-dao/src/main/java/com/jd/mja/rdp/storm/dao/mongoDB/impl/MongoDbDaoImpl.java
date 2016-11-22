package com.jd.mja.rdp.storm.dao.mongoDB.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.mja.rdp.storm.dao.mongoDB.MongoDbDao;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangchaolei on 2016/9/21.
 */
public class MongoDbDaoImpl implements MongoDbDao {
    private MongoCollection<Document> collection= MongoDbFactory.getDeviceConnection();
    private static Logger log = LoggerFactory.getLogger(MongoDbDaoImpl.class);


    @Override
    public void saveDevice(String _id, JSONObject json) {
        BsonDocument filter=new BsonDocument();
        filter.put("_id",new BsonString(_id));

        String s=json.toString();
        Document doc=Document.parse(s);
        doc.append("_id",_id);
         UpdateOptions c=new UpdateOptions();
        c.upsert(true);
        collection.replaceOne(filter,doc,c);

    }


    @Override
    public JSONObject queryDeviceId(final String _id) {
        try {
            BsonDocument filter=new BsonDocument();
            filter.put("_id",new BsonString(_id));
            final JSONObject[] json = {null};
            collection.find(filter).forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    log.info("MongoDbDaoImpl.queryDeviceId.json,{}:{}",_id,document.toJson());
                    json[0] =JSONObject.parseObject(document.toJson());
                }
            });
            return json[0];
        } catch (Exception e) {
            log.error("MongoDbDaoImpl.queryDeviceId.error",e);
            return null;
        }
    }
}
