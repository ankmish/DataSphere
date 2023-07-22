package com.data.collector.services;

import com.data.collector.dao.RuleDao;
import com.data.collector.dto.RuleDTO;
import com.data.collector.models.Rule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final RuleDao ruleDao;

    @Autowired
    public RuleService(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    public Rule createRule(String partnerId, RuleDTO ruleDTO) {
        Rule rule = new Rule();
        rule.setName(ruleDTO.getName());
        rule.setCondition(ruleDTO.getCondition());
        rule.setAction(ruleDTO.getAction());
        rule.setPartnerId(partnerId);
        return ruleDao.saveRule(rule);
    }

    public List<Rule> getRulesByIds(List<String> ruleIds) {
        List<Rule> appliedRules = new ArrayList<>();
        for (String ruleId : ruleIds) {
            Rule rule = ruleDao.findRuleById(ruleId);
            if (rule != null) {
                appliedRules.add(rule);
            }
        }
        return appliedRules;
    }
}
