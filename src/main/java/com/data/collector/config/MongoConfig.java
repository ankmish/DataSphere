package com.data.collector.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    private String connectionStr = "mongodb+srv://Cluster27799:Cluster27799@cluster27799.7rqesf8.mongodb.net/?retryWrites=true&w=majority";

    ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionStr))
            .serverApi(serverApi)
            .build();

    @Bean
    public MongoClient mongoClient() {
//        try (MongoClient mongoClient = MongoClients.create(connectionStr)) {
//            MongoDatabase database = mongoClient.getDatabase("data_sphere_db");
//            MongoCollection<Document> collection = database.getCollection("form_responses");
//            Document doc = collection.find(eq("title", "Back to the Future")).first();
//            if (doc != null) {
//                System.out.println(doc.toJson());
//            } else {
//                System.out.println("No matching documents found.");
//            }
//        }
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
