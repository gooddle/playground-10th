package org.example.playground.domain.feed.repository;

import org.example.playground.domain.feed.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepositoy extends JpaRepository<Feed, Long> {
}
