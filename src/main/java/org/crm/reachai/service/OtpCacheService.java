package org.crm.reachai.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OtpCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long OTP_EXPIRATION_MINUTES = 5;

    public void saveOtp(String email, String otp) {
        // Store with expiration
        redisTemplate.opsForValue().set("OTP:" + email, otp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get("OTP:" + email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete("OTP:" + email);
    }
    public void markOtpVerified(String email) {
        redisTemplate.opsForValue().set("OTP_VERIFIED:" + email, "true", OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }
    public boolean canUpdatePassword(String email) {
        String flag = redisTemplate.opsForValue().get("OTP_VERIFIED:" + email);
        System.out.println(flag);
        return "true".equals(flag);
    }
    public void clearOtpVerifiedFlag(String email) {
        redisTemplate.delete("OTP_VERIFIED:" + email);
    }
}
