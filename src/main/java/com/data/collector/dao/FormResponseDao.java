package com.data.collector.dao;

import com.data.collector.dto.FormResponseDTO;
import com.data.collector.models.Rule;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormResponseDao {

    private final MongoClient mongoClient;

    private final RuleDao ruleDao;


    private final String databaseName;
    private final String formResponsesCollection;

    @Autowired
    public FormResponseDao(MongoClient mongoClient, String databaseName, String formResponsesCollection, RuleDao ruleDao) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.formResponsesCollection = formResponsesCollection;
        this.ruleDao = ruleDao;
    }

    public void saveFormResponse(FormResponseDTO formResponse) {
        Rule rule = checkAndApplyRule(formResponse);

        MongoDatabase saasDB = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> formResponseCollection = saasDB.getCollection(formResponsesCollection);

        Document formResponseDocument = new Document("partner_id", formResponse.getPartnerId())
                .append("form_id", formResponse.getFormId())
                .append("question_id", formResponse.getQuestionId())
                .append("answer", formResponse.getAnswer())
                .append("rule_id", rule != null ? rule.getId() : null); // Reference to the applied rule

        formResponseCollection.insertOne(formResponseDocument);
    }

    private Rule checkAndApplyRule(FormResponseDTO formResponse) {
        // Implement the rule-checking logic here based on the form response data
        // If the rule is triggered, apply the associated action and return the rule
        // Otherwise, return null if no rule is triggered
    }
}
