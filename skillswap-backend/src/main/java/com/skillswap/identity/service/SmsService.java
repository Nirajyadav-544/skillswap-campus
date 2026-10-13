package com.skillswap.identity.service;

public interface SmsService {

    void sendOtp(String phoneNumber, String otp);
}