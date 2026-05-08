package org.example.playground.domain.feed.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFeedRequest {
    private String title;
    private String body;
}
