package org.example.playground.domain.shauser.dto.response;

import org.example.playground.domain.shauser.model.ShaUser;

public record ShaSignInResponse(
        Long userId,
        String email,
        String accessToken,
        String refreshToken
) {
    public static ShaSignInResponse from(ShaUser user, String accessToken, String refreshToken) {
        return new ShaSignInResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
    }
}
