package org.example.playground.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.request.RefreshRequest;
import org.example.playground.domain.user.dto.request.SignInRequest;
import org.example.playground.domain.user.dto.request.SignUpRequest;
import org.example.playground.domain.user.dto.response.RefreshResult;
import org.example.playground.domain.user.dto.response.SignInResponse;
import org.example.playground.domain.user.dto.response.SignInResult;
import org.example.playground.domain.user.dto.response.SignUpResponse;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        SignInResult result = userService.signIn(request);
        return ResponseEntity.ok(SignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal UserPrincipal principal) {
        userService.signOut(principal.id().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResult> refresh(@RequestBody RefreshRequest request) {
        if (request.refreshToken() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.refresh(request.refreshToken()));
    }
}
