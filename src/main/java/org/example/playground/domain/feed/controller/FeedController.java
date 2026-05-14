package org.example.playground.domain.feed.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.feed.dto.request.CreateFeedRequest;
import org.example.playground.domain.feed.dto.response.FeedResponse;
import org.example.playground.domain.feed.service.FeedService;
import org.example.playground.infra.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<FeedResponse> createFeed(
            @RequestBody CreateFeedRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(feedService.createFeed(request, principal.id()));
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponse> getFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(feedService.getByFeedId(feedId, principal.id()));
    }

    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeedList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(feedService.getFeedList(principal.id(), size, page));
    }
}
