package com.skillswap.identity.service;

import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendOtp(String phoneNumber, String otp) {
        System.out.println("=================================");
        System.out.println("SMS OTP");
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("OTP: " + otp);
        System.out.println("=================================");
    }
}