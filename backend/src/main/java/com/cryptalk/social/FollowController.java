package com.cryptalk.social;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members/{memberId}")
public class FollowController {
    private final FollowService follows;
    public FollowController(FollowService follows) { this.follows = follows; }

    @PostMapping("/follow") FollowService.FollowStats follow(@AuthenticationPrincipal Jwt jwt, @PathVariable Long memberId) { return follows.follow(id(jwt), memberId); }
    @DeleteMapping("/follow") FollowService.FollowStats unfollow(@AuthenticationPrincipal Jwt jwt, @PathVariable Long memberId) { return follows.unfollow(id(jwt), memberId); }
    @GetMapping("/social") FollowService.FollowStats stats(@AuthenticationPrincipal Jwt jwt, @PathVariable Long memberId) { return follows.stats(jwt == null ? null : id(jwt), memberId); }
    @GetMapping("/followers") List<FollowService.MemberSummary> followers(@PathVariable Long memberId) { return follows.followers(memberId); }
    @GetMapping("/following") List<FollowService.MemberSummary> following(@PathVariable Long memberId) { return follows.following(memberId); }
    private Long id(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }
}
