package com.data.collector.dao;


import com.data.collector.models.Rule;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RuleDao {
    private final MongoCollection<Document> ruleCollection;

    @Autowired
    public RuleDao(MongoClient mongoClient, String databaseName) {
        MongoDatabase saasDB = mongoClient.getDatabase(databaseName);
        this.ruleCollection = saasDB.getCollection("rule");
    }

    public Rule saveRule(Rule rule) {
        Document ruleDocument = new Document("_id", new ObjectId())
                .append("name", rule.getName())
                .append("condition", rule.getCondition())
                .append("action", rule.getAction())
                .append("partner_id", rule.getPartnerId());

        ruleCollection.insertOne(ruleDocument);
        ObjectId ruleId = ruleDocument.getObjectId("_id");
        rule.setId(ruleId);
        return rule;
    }

    public Rule findRuleByIdAndPartner(String id, String partnerId) {
        Document query = new Document("_id", new ObjectId(id))
                .append("partner_id", partnerId);
        Document ruleDocument = ruleCollection.find(query).first();
        if (ruleDocument != null) {
            return ruleFromDocument(ruleDocument);
        }
        return null;
    }

    public List<Rule> findAllRulesByPartner(String partnerId) {
        List<Rule> rules = new ArrayList<>();
        Document query = new Document("partner_id", partnerId);
        FindIterable<Document> ruleDocuments = ruleCollection.find(query);
        for (Document ruleDocument : ruleDocuments) {
            rules.add(ruleFromDocument(ruleDocument));
        }
        return rules;
    }

    public void updateRule(Rule rule) {
        Document query = new Document("_id", rule.getId())
                .append("partner_id", rule.getPartnerId());
        Document update = new Document("$set", new Document("name", rule.getName())
                .append("condition", rule.getCondition())
                .append("action", rule.getAction()));
        ruleCollection.updateOne(query, update);
    }

    public void deleteRule(String id, String partnerId) {
        Document query = new Document("_id", new ObjectId(id))
                .append("partner_id", partnerId);
        ruleCollection.deleteOne(query);
    }

    public Rule findRuleById(String ruleId) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(ruleId);
        } catch (IllegalArgumentException e) {
            return null; // Invalid ObjectId format, return null
        }

        Document query = new Document("_id", objectId);
        Document ruleDocument = ruleCollection.find(query).first();
        if (ruleDocument != null) {
            return ruleFromDocument(ruleDocument);
        }
        return null;
    }

    private Rule ruleFromDocument(Document ruleDocument) {
        ObjectId id = ruleDocument.getObjectId("_id");
        String name = ruleDocument.getString("name");
        String condition = ruleDocument.getString("condition");
        String action = ruleDocument.getString("action");
        String partnerId = ruleDocument.getString("partner_id");
        return new Rule(id, name, condition, action, partnerId);
    }
}

