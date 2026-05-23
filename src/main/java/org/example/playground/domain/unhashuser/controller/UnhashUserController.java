package org.example.playground.domain.unhashuser.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.unhashuser.dto.request.UnhashRefreshRequest;
import org.example.playground.domain.unhashuser.dto.request.UnhashSignInRequest;
import org.example.playground.domain.unhashuser.dto.request.UnhashSignUpRequest;
import org.example.playground.domain.unhashuser.dto.response.UnhashRefreshResult;
import org.example.playground.domain.unhashuser.dto.response.UnhashSignInResponse;
import org.example.playground.domain.unhashuser.dto.response.UnhashSignInResult;
import org.example.playground.domain.unhashuser.dto.response.UnhashSignUpResponse;
import org.example.playground.domain.unhashuser.service.UnhashUserService;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unhash-users")
@RequiredArgsConstructor
public class UnhashUserController {

    private final UnhashUserService unhashUserService;

    @PostMapping("/signup")
    public ResponseEntity<UnhashSignUpResponse> signUp(@RequestBody UnhashSignUpRequest request) {
        return ResponseEntity.ok(unhashUserService.signUp(request));
    }

    @PostMapping("/signIn/db")
    public ResponseEntity<UnhashSignInResponse> signInByDb(@RequestBody UnhashSignInRequest request) {
        UnhashSignInResult result = unhashUserService.signInByDb(request);
        return ResponseEntity.ok(UnhashSignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/signIn/java")
    public ResponseEntity<UnhashSignInResponse> signInByJava(@RequestBody UnhashSignInRequest request) {
        UnhashSignInResult result = unhashUserService.signInByJava(request);
        return ResponseEntity.ok(UnhashSignInResponse.from(result.user(), result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal UserPrincipal principal) {
        unhashUserService.signOut(principal.id().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<UnhashRefreshResult> refresh(@RequestBody UnhashRefreshRequest request) {
        if (request.refreshToken() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(unhashUserService.refresh(request.refreshToken()));
    }
}
