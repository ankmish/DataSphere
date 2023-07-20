package com.data.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormResponseDTO {

    private ObjectId ruleId;
    private String partnerId;
    private String formId;
    private String questionId;
    private String answer;

    // Getters and setters
}
