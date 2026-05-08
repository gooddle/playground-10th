package org.example.playground.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.feed.dto.request.CreateFeedRequest;
import org.example.playground.domain.feed.dto.response.FeedResponse;
import org.example.playground.domain.feed.model.Feed;
import org.example.playground.domain.feed.repository.FeedRepositoy;
import org.example.playground.domain.user.model.User;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepositoy feedRepository;
    private final UserRepository userRepository;


    @Transactional
    public FeedResponse createFeed(
            CreateFeedRequest request,
            Long userId
    )  {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));
        Feed newFeed = Feed.builder()
                .title(request.getTitle())
                .user(user)
                .body(request.getBody())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
        Feed savedFeed = feedRepository.save(newFeed);
        return FeedResponse.from(savedFeed);
    }

    public FeedResponse getByFeedId(
            Long feedId,
            Long userId
    ){
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not Found"));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("Feed not Found"));
        return FeedResponse.from(feed);
    }

    public Page<FeedResponse> getFeedList(
            Long userId,
            Integer size,
            Integer page       // offset → page
    ) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not Found"));

        Pageable pageable = PageRequest.of(page, size);
        return feedRepository.findAll(pageable)
                .map(FeedResponse::from);
    }
}
