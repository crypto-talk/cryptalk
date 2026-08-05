package com.cryptalk.comment;

import com.cryptalk.common.ApiException;
import com.cryptalk.member.Member;
import com.cryptalk.post.PostService;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository comments; private final PostService posts;
    public CommentService(CommentRepository comments, PostService posts) { this.comments = comments; this.posts = posts; }
    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long postId) { posts.post(postId); return comments.findByPostIdOrderByCreatedAt(postId).stream().map(this::response).toList(); }
    @Transactional
    public CommentResponse create(Long memberId, Long postId, String content) { return response(comments.save(new Comment(posts.post(postId), posts.member(memberId), content))); }
    @Transactional
    public void delete(Long memberId, Long commentId) {
        Comment comment = comments.findById(commentId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));
        posts.own(memberId, comment.getMember().getId()); comments.delete(comment);
    }
    private CommentResponse response(Comment comment) {
        Member member = comment.getMember(); return new CommentResponse(comment.getId(), member.getId(), member.getNickname(), member.getAvatarColor(), comment.getContent(), comment.getCreatedAt());
    }
    public record CommentResponse(Long id, Long memberId, String nickname, String avatarColor, String content, Instant createdAt) {}
}
