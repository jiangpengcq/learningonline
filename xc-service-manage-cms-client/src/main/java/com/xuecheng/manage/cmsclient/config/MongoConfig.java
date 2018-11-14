package com.xuecheng.manage.cmsclient.config;

import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MongoDB配置类
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    protected String db;

    //配置GridFSBucket（用于下载GridFS中储存的文件）
    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
        return GridFSBuckets.create(mongoClient.getDatabase(db));
    }
}
