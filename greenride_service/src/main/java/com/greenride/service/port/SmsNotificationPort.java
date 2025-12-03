package com.greenride.service.port;

public interface SmsNotificationPort {
    void sendSms(String phoneNumber, String message);
}