package org.example.playground.domain.shauser.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.shauser.dto.request.ShaRefreshRequest;
import org.example.playground.domain.shauser.dto.request.ShaSignInRequest;
import org.example.playground.domain.shauser.dto.request.ShaSignUpRequest;
import org.example.playground.domain.shauser.dto.response.ShaRefreshResult;
import org.example.playground.domain.shauser.dto.response.ShaSignInResponse;
import org.example.playground.domain.shauser.dto.response.ShaSignInResult;
import org.example.playground.domain.shauser.dto.response.ShaSignUpResponse;
import org.example.playground.domain.shauser.service.ShaUserService;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sha-users")
@RequiredArgsConstructor
public class ShaUserController {

    private final ShaUserService shaUserService;

    @PostMapping("/signup")
    public ResponseEntity<ShaSignUpResponse> signUp(@RequestBody ShaSignUpRequest request) {
        return ResponseEntity.ok(shaUserService.signUp(request));
    }

    @PostMapping("/signIn/db")
    public ResponseEntity<ShaSignInResponse> signInByDb(@RequestBody ShaSignInRequest request) {
        ShaSignInResult result = shaUserService.signInByDb(request);
        return ResponseEntity.ok(ShaSignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/signIn/db-hash")
    public ResponseEntity<ShaSignInResponse> signInByDbHash(@RequestBody ShaSignInRequest request) {
        ShaSignInResult result = shaUserService.signInByDbHash(request);
        return ResponseEntity.ok(ShaSignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/signIn/java")
    public ResponseEntity<ShaSignInResponse> signInByJava(@RequestBody ShaSignInRequest request) {
        ShaSignInResult result = shaUserService.signInByJava(request);
        return ResponseEntity.ok(ShaSignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal UserPrincipal principal) {
        shaUserService.signOut(principal.id().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ShaRefreshResult> refresh(@RequestBody ShaRefreshRequest request) {
        if (request.refreshToken() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(shaUserService.refresh(request.refreshToken()));
    }
}
