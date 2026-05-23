package org.example.playground.domain.unhashuser.dto.response;

import org.example.playground.domain.unhashuser.model.UnhashUser;

public record UnhashSignInResult(String accessToken, String refreshToken, UnhashUser user) {
}
