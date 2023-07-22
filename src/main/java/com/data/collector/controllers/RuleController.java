package com.data.collector.controllers;

import com.data.collector.dto.RuleDTO;
import com.data.collector.models.Rule;
import com.data.collector.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-sphere/rule")
public class RuleController {

    private final RuleService ruleService;

    @Autowired
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }


    @PostMapping("/{partnerId}")
    public ResponseEntity<Rule> createRule(@PathVariable String partnerId, @RequestBody RuleDTO ruleDTO) {
        Rule createdRule = ruleService.createRule(partnerId, ruleDTO);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

}


/*

POST /api/rules/partner1

{
  "name": "Slangs Search Rule",
  "condition": "contains(slangs, city)",
  "action": "search for slangs in local language based on the city"
}

POST /api/rules/partner2

{
  "name": "Business Rules Validation",
  "condition": "monthlySavings > monthlyIncome",
  "action": "Flag response for fixing: Monthly savings cannot be more than monthly income."
}


POST /api/rules/partner3

{
  "name": "Google Sheets Integration",
  "condition": "true", // Here, the condition is always true, indicating that this rule applies to all responses.
  "action": "Export response data to Google Sheets and generate graphs/charts."
}

POST /api/rules/partner4

{
  "name": "Customer Receipt SMS",
  "condition": "true", // Here, the condition is always true, indicating that this rule applies to all responses.
  "action": "Send an SMS to the customer with response details as a receipt."
}



 */