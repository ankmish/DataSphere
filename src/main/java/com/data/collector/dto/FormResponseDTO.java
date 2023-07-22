package com.data.collector.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormResponseDTO {

    private List<ObjectId> ruleIds;
    private String partnerId;
    private String formId; // unique id per form between tenant and SaaS
    private List<QuestionAnswerDTO> questionAnswers;
    private String answer;
}
