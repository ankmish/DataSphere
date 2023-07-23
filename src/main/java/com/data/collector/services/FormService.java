package com.data.collector.services;

import com.data.collector.dao.FormResponseDao;
import com.data.collector.dto.FormRequestDTO;
import com.data.collector.dto.QuestionAnswerDTO;
import com.data.collector.models.Rule;
import com.data.collector.utils.GoogleSheetsIntegration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    private FormResponseDao formResponseDao;

    private RuleService ruleService;

    private SmsService smsService;

    @Value("${sms.apiKey}") // Load the apiKey value from properties or configuration
    private String apiKey;

    @Value("${sms.apiSecret}") // Load the apiSecret value from properties or configuration
    private String apiSecret;

    Map<String, List<String>> slangDictionary;
    List<String> slangsForCity1;
    List<String> slangsForCity2;


    public FormService(FormResponseDao formResponseDao, RuleService ruleService, SmsService smsService) {
        this.formResponseDao = formResponseDao;
        this.ruleService = ruleService;
        this.smsService = smsService;
        this.slangDictionary = new HashMap<>();
        this.slangsForCity1 = new ArrayList<>();
        this.slangsForCity2 = new ArrayList<>();
        slangsForCity1.add("slang1");
        slangsForCity1.add("slang2");
        slangDictionary.put("City1", slangsForCity1);
        slangsForCity2.add("slang3");
        slangsForCity2.add("slang4");
        slangsForCity2.add("slang5");
        slangDictionary.put("City2", slangsForCity2);
    }

    public void saveFormResponse(FormRequestDTO formRequest) {
        formResponseDao.saveFormResponse(formRequest);
        // Take action based on the applicable rule // TODO: this should be Kafka event that is published an other service will do the processing]]
        List<String> ruleIds = getRuleIdsForForm(formRequest.getFormId());
        List<Rule> rules = ruleService.getRulesByIds(ruleIds);
        if (rules != null) {
            executeRuleAction(rules, formRequest);
        }
    }

    private List<String> getRuleIdsForForm(String formId) {
        return formResponseDao.findRuleIdsByFormId(formId);
    }

    private void executeRuleAction(List<Rule> rules, FormRequestDTO formResponse) {
        for (Rule rule : rules) {
            if ("MonthlySavingsValidation".equals(rule.getName())) {
                double monthlySavings = getMonthlySavings(formResponse);
                double monthlyIncome = getMonthlyIncome(formResponse);
                if (monthlySavings > monthlyIncome) {
                    // Rule triggered: Monthly savings cannot be more than monthly income
                    // TODO: trigger the actions accordingly
                }
            } else if ("Search Slangs".equals(rule.getName())) {
                // Take action for the "Search Slangs" rule
                List<String> slangs = searchSlangs(formResponse);
                if (!slangs.isEmpty()) {
                    handleSlangs(slangs, formResponse);
                }
            } else if ("Google Sheets Integration".equals(rule.getName())) {
                // Take action for Google Sheets Integration rule
                // Implement code to export formResponse data to Google Sheets and generate graphs/charts
                // You might use Google Sheets API or other integration methods
                // Take action for Google Sheets Integration rule
                GoogleSheetsIntegration.exportToGoogleSheets(formResponse);
                System.out.println("Exporting form response to Google Sheets and generating graphs...");
            } else if ("Customer Receipt SMS".equals(rule.getName())) {
                // Take action for Customer Receipt SMS rule
                // Implement code to send an SMS to the customer with response details as a receipt
                // Use an SMS service or provider to send the SMS
                String recipientPhoneNumber = "+918707049831"; // Replace with the actual recipient's phone number
                String message = "Thank you for participating! Here are your details: " + formResponse.getQuestionAnswers();
                smsService.sendSms(recipientPhoneNumber, message);
            }
        }
    }

    private double getMonthlySavings(FormRequestDTO formResponse) {
        // Assuming the monthly savings is stored in the response with the questionId "monthlySavings"
        String monthlySavingsString = null;
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            if ("monthlySavings".equals(questionAnswer.getQuestionId())) {
                monthlySavingsString = questionAnswer.getAnswer();
                break;
            }
        }

        if (monthlySavingsString != null) {
            return Double.parseDouble(monthlySavingsString);
        } else {
            // Return 0 or any other default value if the monthlySavings is not found or not provided in the response
            return 0.0;
        }
    }


    private double getMonthlyIncome(FormRequestDTO formResponse) {
        // Assuming the monthly income is stored in the response with the questionId "monthlyIncome"
        String monthlyIncomeString = null;
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            if ("monthlyIncome".equals(questionAnswer.getQuestionId())) {
                monthlyIncomeString = questionAnswer.getAnswer();
                break;
            }
        }

        if (monthlyIncomeString != null) {
            return Double.parseDouble(monthlyIncomeString);
        } else {
            // Return 0 or any other default value if the monthlyIncome is not found or not provided in the response
            return 0.0;
        }
    }



    private List<String> searchSlangs(FormRequestDTO formResponse) {
        // Get the answer to the text question
        String textAnswer = null;
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            if (questionAnswer.getQuestionId().equals("cities")) {  // identifier for questions // TODO: make generic
                textAnswer = questionAnswer.getAnswer();
                break;
            }
        }

        if (textAnswer == null) {
            // Text question not found in the form response
            return Collections.emptyList();
        }

        // Perform slang search logic based on the text answer and cities
        List<String> slangs = new ArrayList<>();
        // Example logic: Search for slangs in the text answer based on the cities
        // (You can replace this with your actual logic for slang search)
        // Example: Assuming the slangs are stored in a dictionary (HashMap) with city as the key
        // and the list of slangs as the value
        List<String> slangsForCity = slangDictionary.get(textAnswer);
        if (slangsForCity != null) {
            for (String slang : slangsForCity) {
                if (textAnswer.contains(slang)) {
                    slangs.add(slang);
                }
            }
        }
        return slangs;
    }


    private void handleSlangs(List<String> slangs, FormRequestDTO formResponse) {
        // Perform the action for the found slangs (e.g., notify someone or store the information)
        // Example action: Print the found slangs
        System.out.println("Found slangs: " + slangs);
    }

    // Rest of the DAO methods using the MongoClient
}
