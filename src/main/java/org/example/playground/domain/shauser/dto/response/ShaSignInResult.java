package org.example.playground.domain.shauser.dto.response;

import org.example.playground.domain.shauser.model.ShaUser;

public record ShaSignInResult(String accessToken, String refreshToken, ShaUser user) {
}
