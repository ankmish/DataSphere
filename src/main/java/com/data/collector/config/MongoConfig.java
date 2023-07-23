package com.data.collector.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        // mongodb://localhost:27017
        return MongoClients.create("mongodb+srv://cluster27799.7rqesf8.mongodb.net");
    }

    @Bean
    public String databaseName() {
        return "data_sphere_db"; // Your MongoDB database name
    }

    @Bean
    public String formResponsesCollection() {
        return "form_responses"; // Your MongoDB collection name for form responses
    }
}
