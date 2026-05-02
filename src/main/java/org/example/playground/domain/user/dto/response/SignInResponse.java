package org.example.playground.domain.user.dto.response;

import org.example.playground.domain.user.model.User;

public record SignInResponse(
        Long userId,
        String email
) {
    public static SignInResponse from(User user) {
        return new SignInResponse(user.getId(), user.getEmail());
    }
}
