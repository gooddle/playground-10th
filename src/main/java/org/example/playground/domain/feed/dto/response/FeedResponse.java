package org.example.playground.domain.feed.dto.response;

import org.example.playground.domain.feed.model.Feed;

import java.time.LocalDateTime;

public record FeedResponse(
        Long id,
        Long userId,
        String title,
        String body,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FeedResponse from(Feed feed) {
        return new FeedResponse(
                feed.getId(),
                feed.getUser().getId(),
                feed.getTitle(),
                feed.getBody(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}
