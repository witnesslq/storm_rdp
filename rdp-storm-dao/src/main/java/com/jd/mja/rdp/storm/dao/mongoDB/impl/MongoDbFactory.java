 package com.jd.mja.rdp.storm.dao.mongoDB.impl;


 import com.jd.mja.rdp.storm.util.InitProperties;
 import com.mongodb.MongoClient;
 import com.mongodb.ServerAddress;
 import com.mongodb.client.MongoCollection;
 import com.mongodb.client.MongoDatabase;
 import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.Logger;
 import org.bson.BsonDocument;
 import org.bson.BsonString;
 import org.bson.BsonValue;
 import org.bson.Document;
 import org.bson.conversions.Bson;

 import java.util.ArrayList;
 import java.util.List;

 public class MongoDbFactory {

     private static Logger logger = Logger.getLogger(MongoDbFactory.class);
     private static MongoDatabase db = null;
     private static MongoClient mongoClient = null;
     private static InitProperties props = null;
     static{
         initMongDb();
     }
     private static void initMongDb(){
         props=new InitProperties("prop.mongodb");
         try {
             String url = props.read("mongodb.url");
             String port = props.read("mongodb.port");
             String database=props.read("mongodb.database");
             if(StringUtils.isBlank(url) || StringUtils.isBlank(port)){
                 logger.error("mongodb.url is null or mongodb.port is null");
                 throw new RuntimeException("mongodb.url is null or mongodb.port is null");
             }else{
                 List<ServerAddress> seeds = new ArrayList<ServerAddress>();
                 String[] urls = url.split(",");
                 String[] ports = port.split(",");
                 for(int i=0;i<urls.length;i++){
                     ServerAddress address = new ServerAddress(urls[i],Integer.valueOf(ports[i]));
                     seeds.add(address);
                 }
                 mongoClient = new MongoClient(seeds);
                 db = mongoClient.getDatabase(database);
                 if(db == null){
                     throw new RuntimeException("mongodb db init error");
                 }
             }
         } catch (Exception e) {
             throw new RuntimeException("load mongodb config file failed");
         }
         logger.info("mongodb init success");
     }
     public static MongoCollection<Document> getConnection(String collectionName){
         if(db==null){
             initMongDb();
         }
         return db.getCollection(collectionName);
     }
     public static MongoCollection<Document> getDeviceConnection(){
          return MongoDbFactory.getConnection(props.read("mongodb.collection.device"));
     }



 }
