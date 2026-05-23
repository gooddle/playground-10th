package org.example.playground.domain.unhashuser.dto.response;

import org.example.playground.domain.unhashuser.model.UnhashUser;

public record UnhashSignInResponse(
        Long userId,
        String email,
        String accessToken,
        String refreshToken
) {
    public static UnhashSignInResponse from(UnhashUser user, String accessToken, String refreshToken) {
        return new UnhashSignInResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
    }
}
