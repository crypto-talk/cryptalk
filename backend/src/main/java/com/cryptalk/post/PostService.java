package com.cryptalk.post;

import com.cryptalk.asset.AssetService;
import com.cryptalk.asset.AssetSnapshot;
import com.cryptalk.coin.Coin;
import com.cryptalk.coin.CoinRepository;
import com.cryptalk.comment.CommentRepository;
import com.cryptalk.common.ApiException;
import com.cryptalk.member.AssetVisibility;
import com.cryptalk.member.Member;
import com.cryptalk.member.MemberRepository;
import com.cryptalk.post.PostDtos.*;
import com.cryptalk.wallet.WalletRepository;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository posts; private final PostLikeRepository likes; private final CommentRepository comments;
    private final MemberRepository members; private final CoinRepository coins; private final WalletRepository wallets; private final AssetService assets;
    public PostService(PostRepository posts, PostLikeRepository likes, CommentRepository comments, MemberRepository members,
                       CoinRepository coins, WalletRepository wallets, AssetService assets) {
        this.posts=posts; this.likes=likes; this.comments=comments; this.members=members; this.coins=coins; this.wallets=wallets; this.assets=assets;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> list(String symbol, Long viewerId, int size) {
        if (coins.findBySymbolIgnoreCaseAndActiveTrue(symbol).isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다.");
        return posts.findByCoinSymbolIgnoreCaseOrderByCreatedAtDesc(symbol, PageRequest.of(0, Math.min(Math.max(size, 1), 100)))
            .stream().map(post -> response(post, viewerId)).toList();
    }

    @Transactional
    public PostResponse create(Long memberId, CreatePostRequest request) {
        Member member = member(memberId);
        Coin coin = coins.findBySymbolIgnoreCaseAndActiveTrue(request.coinSymbol()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다."));
        AssetSnapshot snapshot = assets.snapshotForPost(memberId, coin.getId());
        boolean verified = snapshot != null && snapshot.isVerified();
        BigDecimal value = verified ? snapshot.getValueKrw() : null;
        return response(posts.save(new Post(member, coin, request.title(), request.content(), value, verified)), memberId);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = post(postId); own(memberId, post.getMember().getId()); posts.delete(post);
    }

    @Transactional
    public PostResponse like(Long memberId, Long postId) {
        Post post = post(postId); PostLikeId id = new PostLikeId(postId, memberId);
        if (!likes.existsById(id)) likes.save(new PostLike(post, member(memberId)));
        return response(post, memberId);
    }

    @Transactional
    public PostResponse unlike(Long memberId, Long postId) {
        likes.deleteById(new PostLikeId(postId, memberId)); return response(post(postId), memberId);
    }

    public Post post(Long id) { return posts.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.")); }
    public Member member(Long id) { return members.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.")); }
    public void own(Long actor, Long owner) { if (!actor.equals(owner)) throw new ApiException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다."); }

    private PostResponse response(Post post, Long viewerId) {
        Member author = post.getMember();
        String address = wallets.findFirstByMemberId(author.getId()).map(wallet -> mask(wallet.getAddress())).orElse(null);
        boolean visible = author.getAssetVisibility() != AssetVisibility.HIDDEN;
        BigDecimal value = visible ? post.getAuthorAssetValueKrw() : null;
        String display = assetDisplay(author.getAssetVisibility(), value);
        boolean liked = viewerId != null && likes.existsById(new PostLikeId(post.getId(), viewerId));
        return new PostResponse(post.getId(), post.getCoin().getSymbol(), post.getTitle(), post.getContent(),
            new AuthorResponse(author.getId(), author.getNickname(), author.getAvatarColor(), address),
            visible && post.isAuthorVerified(), value, display, likes.countByPostId(post.getId()), comments.countByPostId(post.getId()), liked, post.getCreatedAt());
    }

    private String assetDisplay(AssetVisibility visibility, BigDecimal value) {
        if (visibility == AssetVisibility.HIDDEN || value == null) return "자산 비공개";
        if (visibility == AssetVisibility.RANGE) {
            long amount = value.longValue();
            if (amount < 10_000_000) return "1천만원 미만";
            if (amount < 100_000_000) return "1천만~1억원";
            if (amount < 1_000_000_000) return "1억~10억원";
            return "10억원 이상";
        }
        return "₩" + NumberFormat.getIntegerInstance(Locale.KOREA).format(value);
    }
    private String mask(String address) { return address.length() < 12 ? address : address.substring(0, 6) + "..." + address.substring(address.length()-4); }
}
