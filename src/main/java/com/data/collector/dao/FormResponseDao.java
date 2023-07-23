package com.data.collector.dao;

import com.data.collector.dto.FormRequestDTO;
import com.data.collector.dto.QuestionAnswerDTO;
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
public class FormResponseDao {

    private final MongoClient mongoClient;

    private final String databaseName;
    private final String formResponsesCollection;

    private MongoDatabase saasDB;

    private MongoCollection<Document> formResponseCollection;

    @Autowired
    public FormResponseDao(MongoClient mongoClient, String databaseName, String formResponsesCollection, RuleDao ruleDao) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.formResponsesCollection = formResponsesCollection;
        this.saasDB = mongoClient.getDatabase(databaseName);
        this.formResponseCollection = saasDB.getCollection(formResponsesCollection);
    }

    public void saveFormResponse(FormRequestDTO formResponse) {
        List<Document> questionAnswerDocuments = new ArrayList<>();
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            Document questionAnswerDocument = new Document("question_id", questionAnswer.getQuestionId())
                    .append("question", questionAnswer.getQuestion())
                    .append("answer", questionAnswer.getAnswer());
            questionAnswerDocuments.add(questionAnswerDocument);
        }

        Document formResponseDocument = new Document("partner_id", formResponse.getPartnerId())
                .append("form_id", formResponse.getFormId())
                .append("rule_ids", formResponse.getRuleIds() != null ? formResponse.getRuleIds() : null) // Reference to the applied rule, multiple rules can be applied to it [assuming, order stored from left to right]
                .append("question_answers", questionAnswerDocuments);
        formResponseCollection.insertOne(formResponseDocument);
    }

    public List<String> findRuleIdsByFormId(String formId) {
        List<String> ruleIds = new ArrayList<>();
        Document query = new Document("form_id", formId);
        List<Document> formResponses = formResponseCollection.find(query).into(new ArrayList<>());
        for (Document formResponseDoc : formResponses) {
            List<ObjectId> ruleObjectIds = (List<ObjectId>) formResponseDoc.get("rule_ids");
            if (ruleObjectIds != null && !ruleObjectIds.isEmpty()) {
                for (ObjectId ruleObjectId : ruleObjectIds) {
                    ruleIds.add(ruleObjectId.toString());
                }
            }
        }
        return ruleIds;
    }

    public List<Document> findFormByFormId(String formId) {
        Document query = new Document("form_id", formId);
        return formResponseCollection.find(query).into(new ArrayList<>());
    }
}
