package com.data.collector.services;

import com.data.collector.dao.FormResponseDao;
import com.data.collector.dto.FormResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    @Autowired
    private FormResponseDao formResponseDao;

    public void saveFormResponse(FormResponseDTO formResponse) {
        formResponseDao.saveFormResponse(formResponse);
    }
}
