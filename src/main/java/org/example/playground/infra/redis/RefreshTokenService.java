package org.example.playground.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public void save(String userId, String refreshToken, long expirationDays) {
        redisTemplate.opsForValue()
                .set(PREFIX + userId, refreshToken, Duration.ofDays(expirationDays));
    }

    public boolean isValid(String userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return refreshToken.equals(stored);
    }

    public void delete(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
