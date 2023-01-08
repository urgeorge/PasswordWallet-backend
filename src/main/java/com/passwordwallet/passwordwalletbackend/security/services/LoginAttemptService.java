package com.passwordwallet.passwordwalletbackend.security.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.time.Instant;

import com.passwordwallet.passwordwalletbackend.repository.LastLoginInfoRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    @Autowired
    LastLoginInfoRepository lastLoginInfoRepository;

    // Set the number of failed attempts before an IP is blocked
    private static final int BLOCK_AFTER_FAILS = 4;

    // Set the time periods for blocking IPs
    private static final Duration BLOCK_FOR_5_MINUTES = Duration.ofMinutes(5);
    private static final Duration BLOCK_FOR_2_HOURS = Duration.ofHours(2);

    // Use a Guava LoadingCache to store IP addresses and their associated login attempts
    private LoadingCache<String, LoginAttempt> ipLoginAttempts = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, LoginAttempt>() {
                @Override
                public LoginAttempt load(String ip) {
                    return new LoginAttempt();
                }
            });

    public void recordLoginAttempt(Long userId, String ip, boolean success) {
        LoginAttempt attempt = ipLoginAttempts.getUnchecked(ip);
        if (success) {
            this.saveLastLoginInfo(userId, true);
            attempt.setLastSuccess(Instant.now());
            attempt.setFails(0);
            attempt.setBlockExpires(null);
        } else {
            this.saveLastLoginInfo(userId, false);
            attempt.setLastFail(Instant.now());
            attempt.setFails(attempt.getFails() + 1);
            if (attempt.getFails() == 2) {
                attempt.setBlockExpires(Instant.now().plus(BLOCK_FOR_5_MINUTES));
            } else if (attempt.getFails() == 3) {
                attempt.setBlockExpires(Instant.now().plus(BLOCK_FOR_2_HOURS));
            } else if (attempt.getFails() == BLOCK_AFTER_FAILS) {
                attempt.setBlockExpires(Instant.MAX);
            }
        }
    }

    private void saveLastLoginInfo(Long userId, boolean loginSucceed) {
//        if(lastLoginInfoRepository.findByUserId(userId).isPresent()){
//            loginSucceed ? lastLoginInfoRepository
//        }
//        if(loginSucceed)
    }

    public boolean isIpBlocked(String ip) {
        LoginAttempt attempt = ipLoginAttempts.getUnchecked(ip);
        return attempt.getBlockExpires() != null && attempt.getBlockExpires().isAfter(Instant.now());
    }

    public LoginAttempt getLoginInfo(String ip) {
        return ipLoginAttempts.getUnchecked(ip);
    }

    @Data
    private static class LoginAttempt {
        private Instant lastSuccess;
        private Instant lastFail;
        private int fails;
        private Instant blockExpires;
    }
}
