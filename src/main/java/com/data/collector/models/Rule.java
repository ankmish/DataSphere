package com.data.collector.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@Builder
public class Rule {
    private ObjectId id;
    private String name;
    private String condition;
    private String action;

    private String partnerId;

    // Constructors, getters, and setters
}

