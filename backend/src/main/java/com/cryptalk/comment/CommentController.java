package com.cryptalk.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService comments;
    public CommentController(CommentService comments) { this.comments = comments; }
    @GetMapping("/posts/{postId}/comments") List<CommentService.CommentResponse> list(@PathVariable Long postId) { return comments.list(postId); }
    @PostMapping("/posts/{postId}/comments") CommentService.CommentResponse create(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId, @Valid @RequestBody CreateCommentRequest request) { return comments.create(id(jwt), postId, request.content()); }
    @PatchMapping("/comments/{commentId}") CommentService.CommentResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long commentId, @Valid @RequestBody UpdateCommentRequest request) { return comments.update(id(jwt), commentId, request.content()); }
    @DeleteMapping("/comments/{commentId}") ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long commentId) { comments.delete(id(jwt), commentId); return ResponseEntity.noContent().build(); }
    private Long id(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }
    public record CreateCommentRequest(@NotBlank @Size(max=1000) String content) {}
    public record UpdateCommentRequest(@NotBlank @Size(max=1000) String content) {}
}
