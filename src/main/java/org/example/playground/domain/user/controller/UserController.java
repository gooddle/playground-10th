package org.example.playground.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.request.SignInRequest;
import org.example.playground.domain.user.dto.request.SignUpRequest;
import org.example.playground.domain.user.dto.response.SignInResponse;
import org.example.playground.domain.user.dto.response.SignInResult;
import org.example.playground.domain.user.dto.response.SignUpResponse;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.infra.security.CookieProvider;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CookieProvider cookieProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        SignInResult result = userService.signIn(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createAccessCookie(result.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createRefreshCookie(result.refreshToken()).toString())
                .body(SignInResponse.from(result.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal UserPrincipal principal) {
        userService.signOut(principal.id().toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.deleteAccessCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieProvider.deleteRefreshCookie().toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        String newAccessToken = userService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createAccessCookie(newAccessToken).toString())
                .build();
    }
}
