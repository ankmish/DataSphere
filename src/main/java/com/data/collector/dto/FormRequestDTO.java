package com.data.collector.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 9149005191710470697L;
    private List<ObjectId> ruleIds;
    private String partnerId;
    private String formId; // unique id per form between tenant and SaaS
    private List<QuestionAnswerDTO> questionAnswers;
}