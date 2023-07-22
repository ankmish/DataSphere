package com.data.collector.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SlangsService {
    public List<String> searchSlangsByCity(String city) {
        // Your logic to search for slangs based on the city
        // Returns a list of slangs in the local language
        // For simplicity, I'll return a sample list here
        List<String> slangs = new ArrayList<>();
        slangs.add("Slang 1");
        slangs.add("Slang 2");
        return slangs;
    }
}