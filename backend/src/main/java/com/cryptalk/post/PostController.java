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
    @GetMapping("/feed")
    FeedPageResponse feed(@RequestParam(required=false) String cursor, @RequestParam(defaultValue="30") int size,
                          @AuthenticationPrincipal Jwt jwt) {
        return posts.feed(jwt == null ? null : id(jwt), cursor, size);
    }
    @GetMapping("/feed/following")
    FeedPageResponse followingFeed(@RequestParam(required=false) String cursor,
                                   @RequestParam(defaultValue="30") int size,
                                   @AuthenticationPrincipal Jwt jwt) {
        return posts.followingFeed(id(jwt), cursor, size);
    }
    @GetMapping("/posts/{postId}")
    PostResponse get(@PathVariable Long postId, @AuthenticationPrincipal Jwt jwt) {
        return posts.get(postId, jwt == null ? null : id(jwt));
    }
    @PostMapping("/posts") PostResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreatePostRequest request) { return posts.create(id(jwt), request); }
    @PutMapping("/posts/{postId}") PostResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId, @Valid @RequestBody UpdatePostRequest request) { return posts.update(id(jwt), postId, request); }
    @DeleteMapping("/posts/{postId}") ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { posts.delete(id(jwt), postId); return ResponseEntity.noContent().build(); }
    @PostMapping("/posts/{postId}/likes") PostResponse like(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.like(id(jwt), postId); }
    @DeleteMapping("/posts/{postId}/likes") PostResponse unlike(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.unlike(id(jwt), postId); }
    @PostMapping("/posts/{postId}/bookmarks") PostResponse bookmark(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.bookmark(id(jwt), postId); }
    @DeleteMapping("/posts/{postId}/bookmarks") PostResponse unbookmark(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.unbookmark(id(jwt), postId); }
    @GetMapping("/me/bookmarks") List<PostResponse> bookmarks(@AuthenticationPrincipal Jwt jwt) { return posts.bookmarks(id(jwt)); }
    @PostMapping("/posts/{postId}/reposts") PostResponse repost(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.repost(id(jwt), postId); }
    @DeleteMapping("/posts/{postId}/reposts") PostResponse unrepost(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId) { return posts.unrepost(id(jwt), postId); }
    private Long id(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }
}
