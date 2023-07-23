package com.data.collector.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${sms.apiKey}") // Load the apiKey value from properties or configuration
    private String apiKey;

    @Value("${sms.apiSecret}") // Load the apiSecret value from properties or configuration
    private String apiSecret;

    public void sendSms(String recipientPhoneNumber, String message) {
        // Implement your integration with an SMS service provider here
        // This example just prints the SMS details
        System.out.println("Sending SMS to: " + recipientPhoneNumber);
        System.out.println("Message: " + message);
        System.out.println("SMS Sent!");
    }
}
