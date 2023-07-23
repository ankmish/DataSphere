package com.data.collector.services;

import com.data.collector.dao.RuleDao;
import com.data.collector.dto.RuleDTO;
import com.data.collector.models.Rule;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Rule operation related endpoint
 */
@Service
public class RuleService {

    static final Logger logger = Logger.getLogger(String.valueOf(RuleService.class));
    private final RuleDao ruleDao;

    @Autowired
    public RuleService(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    public Rule createRule(String partnerId, RuleDTO ruleDTO) throws Exception {
        logger.info("Rule creation request received from partner: " + partnerId + " with name: " + ruleDTO.getName());
        validateRuleCreationRequest(partnerId, ruleDTO);
        Rule rule = new Rule();
        rule.setName(ruleDTO.getName());
        rule.setCondition(ruleDTO.getCondition());
        rule.setAction(ruleDTO.getAction());
        rule.setPartnerId(partnerId);
        return ruleDao.saveRule(rule);
    }

    private void validateRuleCreationRequest(String partnerId, RuleDTO ruleDTO) throws Exception {
        List<Rule> rules = ruleDao.findRuleByNameAndPartnerId(ruleDTO.getName(), partnerId);
        if(!rules.isEmpty()) {
            throw new Exception("Rule: " + ruleDTO.getName() + " already exists for partner: " + partnerId);
        }
    }

    public List<Rule> getRulesByIdsAndPartnerId(List<String> ruleIds, String partnerId) {
        logger.info("Fetching rules from partner: " + partnerId + " for rulesIds");
        List<Rule> appliedRules = new ArrayList<>();
        for (String ruleId : ruleIds) {
            Rule rule = ruleDao.findRuleByIdAndPartner(ruleId, partnerId);
            if (rule != null) {
                appliedRules.add(rule);
            }
        }
        return appliedRules;
    }
}
