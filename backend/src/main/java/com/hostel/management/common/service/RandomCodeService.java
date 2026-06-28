package com.hostel.management.common.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RandomCodeService
{
    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
