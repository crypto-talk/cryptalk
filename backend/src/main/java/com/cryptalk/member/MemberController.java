package com.cryptalk.member;

import com.cryptalk.asset.AssetService;
import com.cryptalk.auth.AuthDtos.MemberResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
public class MemberController {
    private final MemberService members; private final AssetService assets;
    public MemberController(MemberService members, AssetService assets) { this.members = members; this.assets = assets; }
    @GetMapping MemberResponse me(@AuthenticationPrincipal Jwt jwt) { return members.get(id(jwt)); }
    @PatchMapping MemberResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateMemberRequest request) { return members.update(id(jwt), request.nickname(), request.avatarColor()); }
    @GetMapping("/assets") List<AssetService.AssetResponse> assets(@AuthenticationPrincipal Jwt jwt) { return assets.refreshAndList(id(jwt)); }
    @PatchMapping("/asset-visibility") MemberResponse visibility(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody VisibilityRequest request) { return members.visibility(id(jwt), request.visibility()); }
    private Long id(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }
    public record UpdateMemberRequest(@Size(min=2,max=40) String nickname, @Pattern(regexp="^#[0-9a-fA-F]{6}$") String avatarColor) {}
    public record VisibilityRequest(@NotNull AssetVisibility visibility) {}
}
