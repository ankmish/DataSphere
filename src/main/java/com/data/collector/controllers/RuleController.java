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
    public ResponseEntity<Rule> createRule(@PathVariable String partnerId, @RequestBody RuleDTO ruleDTO) throws Exception {
        Rule createdRule = ruleService.createRule(partnerId, ruleDTO);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

}