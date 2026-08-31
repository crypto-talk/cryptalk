package com.cryptalk.post;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCoinSymbolIgnoreCaseOrderByCreatedAtDesc(String symbol, Pageable pageable);
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
