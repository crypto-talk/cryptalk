package com.cryptalk.post;

import com.cryptalk.post.PostDtos.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PostController {
    private final PostService posts;
    public PostController(PostService posts) { this.posts = posts; }
    @GetMapping("/communities/{symbol}/posts")
    List<PostResponse> list(@PathVariable String symbol, @RequestParam(defaultValue="30") int size, @AuthenticationPrincipal Jwt jwt) {
        return posts.list(symbol, jwt == null ? null : id(jwt), size);
    }
    @PostMapping("/posts") PostResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreatePostRequest request) { return posts.create(id(jwt), request); }
    @DeleteMapping("/posts/{postId}") ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { posts.delete(id(jwt), postId); return ResponseEntity.noContent().build(); }
    @PostMapping("/posts/{postId}/likes") PostResponse like(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.like(id(jwt), postId); }
    @DeleteMapping("/posts/{postId}/likes") PostResponse unlike(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.unlike(id(jwt), postId); }
    private Long id(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }
}
