package com.data.collector.config;

import com.data.collector.services.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsServiceConfig {

    @Value("${sms.apiKey}") // Load the apiKey value from properties or configuration
    private String apiKey;

    @Value("${sms.apiSecret}") // Load the apiSecret value from properties or configuration
    private String apiSecret;

    @Bean
    public SmsService smsService() {
        return new SmsService(apiKey, apiSecret);
    }
}