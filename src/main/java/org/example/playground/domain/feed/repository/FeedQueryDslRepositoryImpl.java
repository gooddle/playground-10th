package org.example.playground.domain.feed.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

//query 문 구현체(검색기능)
//TODO(): comment랑 조인
@RequiredArgsConstructor
public class FeedQueryDslRepositoryImpl implements FeedQueryDslRepository {
    private final JPAQueryFactory queryFactory;
}
