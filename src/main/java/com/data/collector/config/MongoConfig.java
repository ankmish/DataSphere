package com.data.collector.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class contains MongoDB configuration
 */
@Configuration
public class MongoConfig {

    private final String connectionStr = "mongodb+srv://Cluster27799:<password>@cluster27799.7rqesf8.mongodb.net/?retryWrites=true&w=majority";

    ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionStr))
            .serverApi(serverApi)
            .build();

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(settings);
    }
    @Bean
    public String databaseName() {
        return "data_sphere_db";
    }

    @Bean
    public String formResponsesCollection() {
        return "form_responses";
    }
}
