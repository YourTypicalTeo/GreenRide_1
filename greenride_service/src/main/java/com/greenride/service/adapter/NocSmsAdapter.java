package com.greenride.service.adapter;

import com.greenride.service.port.SmsNotificationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NocSmsAdapter implements SmsNotificationPort {

    private final RestTemplate restTemplate;
    private static final String NOC_SMS_URL = "http://localhost:8081/api/v1/sms";

    @Autowired
    public NocSmsAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("e164", phoneNumber);
            payload.put("content", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            // Sending request to External Service (Port 8081)
            restTemplate.postForObject(NOC_SMS_URL, request, String.class);

            System.out.println("✅ [Adapter] SMS sent via NOC to " + phoneNumber);
        } catch (Exception e) {
            System.err.println("❌ [Adapter] Failed to reach NOC service: " + e.getMessage());
        }
    }
}