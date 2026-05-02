package org.example.playground.domain.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.feed.dto.request.CreateFeedRequest;
import org.example.playground.domain.feed.dto.request.UpdateFeedBodyRequest;
import org.example.playground.domain.feed.dto.request.UpdateFeedTitleRequest;
import org.example.playground.domain.feed.dto.response.CreateFeedResponse;
import org.example.playground.domain.feed.dto.response.FeedResponse;
import org.example.playground.domain.feed.model.Feed;
import org.example.playground.domain.feed.repository.FeedRepository;
import org.example.playground.domain.user.model.User;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;


    /**
     * 게시글 생성
     *
     * @param request 게시글 생성 본문
     * @throws IllegalArgumentException 로그인 하지 않은 사용자 접근 시
     * @return 생성 된 게시글 반환
     */
    @Transactional
    public CreateFeedResponse createFeed(CreateFeedRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));
        Feed newFeed = Feed.builder()
                .title(request.getTitle())
                .user(user)
                .body(request.getBody())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
        feedRepository.save(newFeed);
        return CreateFeedResponse.from(newFeed);
    }

    /**
     * 게시글 단건 조회
     *
     * @param feedId feed pk
     * @param userId user pk
     * @return 조회된 게시글 객체 반환
     */
    public FeedResponse getFeedById(Long feedId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
        return FeedResponse.from(feed);
    }


    /**
     * 게시글 제목 수정
     *
     * @param request 게시글 제목 수정 본문
     * @param feedId feed pk
     * @param userId user pk
     * @return 업데이트 된 게시글 객체 반환
     */
    @Transactional
    public FeedResponse patchFeedTitle(UpdateFeedTitleRequest request, Long userId, Long feedId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
        if (!Objects.equals(user.getId(), feed.getUser().getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        //더티 체킹 save 직접 명시 안해줘도 jpa(1차캐쉬)에서 업데이트를 관리해줌
        Feed updateFeed = Feed.builder()
                .title(request.getTitle())
                .updatedAt(LocalDateTime.now())
                .build();
        return FeedResponse.from(updateFeed);
    }

    /**
     * 게시글 본문 수정
     *
     * @param  request 게시글 body 수정 본문
     * @param feedId feed pk
     * @param userId user pk
     * @return 업데이트 된 게시글 객체 반환
     */
    @Transactional
    public FeedResponse patchFeedBody(UpdateFeedBodyRequest request, Long userId, Long feedId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
        if (!Objects.equals(user.getId(), feed.getUser().getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        //더티 체킹 save 직접 명시 안해줘도 jpa(1차캐쉬)에서 업데이트를 관리해줌
        Feed updateFeed = Feed.builder()
                .title(request.getBody())
                .updatedAt(LocalDateTime.now())
                .build();
        return FeedResponse.from(updateFeed);
    }

    /**
     * 게시글 리스트 조회
     *
     * @param userId user pk
     * @return 조회된 게시글 리스트
     *
     * TODO() 페이지네이션 추가
     */
    public List<FeedResponse> getFeedList(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));
        List<Feed> feeds = feedRepository.findAll();
        return feeds.stream()
                .map(FeedResponse::from)
                .toList();
    }
}
