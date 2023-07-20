package com.data.collector.controllers;

import com.data.collector.dto.FormResponseDTO;
import com.data.collector.services.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitFormResponse(@RequestBody FormResponseDTO formResponse) {
        formService.saveFormResponse(formResponse);
        return new ResponseEntity<>("Form response saved successfully.", HttpStatus.CREATED);
    }
}

