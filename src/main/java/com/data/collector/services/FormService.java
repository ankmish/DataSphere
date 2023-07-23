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
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    public static final String MONTHLY_SAVINGS_VALIDATION = "MonthlySavingsValidation";
    public static final String SEARCH_SLANGS = "SearchSlangs";
    public static final String GOOGLE_SHEETS_INTEGRATION = "GoogleSheetsIntegration";
    public static final String CITIES = "cities";
    public static final String CUSTOMER_RECEIPT_SMS = "CustomerReceiptSMS";
    private final FormResponseDao formResponseDao;

    private final RuleService ruleService;

    private final SmsService smsService;

    Map<String, List<String>> slangDictionary;
    List<String> slangsForCity;

    static final Logger logger = Logger.getLogger(String.valueOf(FormService.class));


    public FormService(FormResponseDao formResponseDao, RuleService ruleService, SmsService smsService) {
        this.formResponseDao = formResponseDao;
        this.ruleService = ruleService;
        this.smsService = smsService;
        this.slangDictionary = new HashMap<>();
        this.slangsForCity = new ArrayList<>();
        slangsForCity.add("Namaste");
        slangsForCity.add("Hi");
        slangDictionary.put("Hello", slangsForCity);
    }

    public void saveFormResponse(FormRequestDTO formRequest) throws Exception {
        logger.info("Form submission request received for partner: " + formRequest.getPartnerId() + " with formId: " + formRequest.getFormId());
        // validate if duplicate form request
        validateFormSubmissionRequest(formRequest);
        formResponseDao.saveFormResponse(formRequest);
        logger.info("Form submitted successfully for partner: " + formRequest.getPartnerId() + " with formId: " + formRequest.getFormId() + " publishing event for post analysis");
        publishFormEvent(formRequest);
    }

    // This will be Kafka event which will be consumed by another microservice(rule-engine) to apply rules as per rule action
    private void publishFormEvent(FormRequestDTO formRequest) {
        List<String> ruleIds = getRuleIdsForForm(formRequest.getFormId());
        if(ruleIds.isEmpty()) {
            logger.info("FormId: " + formRequest.getFormId() + " from partner: " + formRequest.getPartnerId() + " no action has to be taken");
            return;
        }
        List<Rule> rules = ruleService.getRulesByIdsAndPartnerId(ruleIds, formRequest.getPartnerId());
        if (rules != null) {
            executeRuleAction(rules, formRequest);
        }

    }

    private void validateFormSubmissionRequest(FormRequestDTO formRequest) throws Exception {
        List<Document> forms = formResponseDao.findFormByFormId(formRequest.getFormId());
        if(!forms.isEmpty()) {
            throw new Exception("Duplicate form submission request for formId: " + formRequest.getFormId() + " from partner: " + formRequest.getPartnerId());
        }
    }

    private List<String> getRuleIdsForForm(String formId) {
        return formResponseDao.findRuleIdsByFormId(formId);
    }

    // This function will be part of rule-engine service
    private void executeRuleAction(List<Rule> rules, FormRequestDTO formResponse) {
        logger.info("Rule Engine: FormId: " + formResponse.getFormId() + " from partner: " + formResponse.getPartnerId() + " number of applicable rules: " + rules.size());
        for (Rule rule : rules) {
            if (MONTHLY_SAVINGS_VALIDATION.equals(rule.getName())) {
                double monthlySavings = getMonthlySavings(formResponse);
                double monthlyIncome = getMonthlyIncome(formResponse);
                if (monthlySavings > monthlyIncome) {
                    logger.info("Rule Engine: FormId: " + formResponse.getFormId() + " from partner: " + formResponse.getPartnerId() + " MONTHLY_SAVINGS_VALIDATION rule triggered, inform partner.");
                }
            } else if (SEARCH_SLANGS.equals(rule.getName())) {
                List<String> slangs = searchSlangs(formResponse);
                if (!slangs.isEmpty()) {
                    logger.info("Rule Engine: FormId: " + formResponse.getFormId() + " from partner: " + formResponse.getPartnerId() + " SEARCH_SLANGS rule triggered, return slangs list : " + slangs);
                }
            } else if (GOOGLE_SHEETS_INTEGRATION.equals(rule.getName())) {
                GoogleSheetsIntegration.exportToGoogleSheets(formResponse);
                System.out.println("Exporting form response to Google Sheets and generating graphs...");
            } else if (CUSTOMER_RECEIPT_SMS.equals(rule.getName())) {
                String recipientPhoneNumber = "+918304059831";
                String message = "Thank you for participating! Here are your details: " + formResponse.getQuestionAnswers();
                smsService.sendSms(recipientPhoneNumber, message);
            }
        }
    }

    private double getMonthlySavings(FormRequestDTO formResponse) {
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
            return 0.0;
        }
    }


    private double getMonthlyIncome(FormRequestDTO formResponse) {
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
            return 0.0;
        }
    }



    private List<String> searchSlangs(FormRequestDTO formResponse) {
        String textAnswer = null;
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            if (questionAnswer.getQuestionId().equals(CITIES)) {
                textAnswer = questionAnswer.getAnswer();
                break;
            }
        }
        if (textAnswer == null) {
            return Collections.emptyList();
        }
        return slangDictionary.get(textAnswer);
    }
}
