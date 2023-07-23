package com.data.collector.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    static final Logger logger = Logger.getLogger(String.valueOf(SmsService.class));

    @Value("${sms.apiKey}")
    private String apiKey;

    @Value("${sms.apiSecret}")
    private String apiSecret;

    // This will be third-party SMS provider like Gupshup
    public void sendSms(String recipientPhoneNumber, String message) {
        logger.info("Sending SMS to receipt: " + recipientPhoneNumber + " with message: " + message);
    }
}
