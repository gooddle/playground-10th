package org.example.playground.domain.feed.dto.response;

import org.example.playground.domain.feed.model.Feed;

import java.time.LocalDateTime;

public record CreateFeedResponse(
        Long id,
        String title,
        String body,
        LocalDateTime createdAt
) {
    public static CreateFeedResponse from(Feed feed) {
        return new CreateFeedResponse(
                feed.getId(),
                feed.getTitle(),
                feed.getBody(),
                feed.getCreatedAt()
        );
    }
}
