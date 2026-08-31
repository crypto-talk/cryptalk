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
import java.net.URI;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository posts; private final PostLikeRepository likes; private final CommentRepository comments;
    private final PostMediaRepository media; private final PostBookmarkRepository bookmarks; private final PostRepostRepository reposts;
    private final MemberRepository members; private final CoinRepository coins; private final WalletRepository wallets; private final AssetService assets;
    public PostService(PostRepository posts, PostLikeRepository likes, CommentRepository comments, MemberRepository members,
                       CoinRepository coins, WalletRepository wallets, AssetService assets, PostMediaRepository media,
                       PostBookmarkRepository bookmarks, PostRepostRepository reposts) {
        this.posts=posts; this.likes=likes; this.comments=comments; this.members=members; this.coins=coins; this.wallets=wallets; this.assets=assets;
        this.media=media; this.bookmarks=bookmarks; this.reposts=reposts;
    }

    private static final Pattern YOUTUBE = Pattern.compile("^(?:https://)?(?:www\\.)?(?:youtube\\.com/(?:shorts/|watch\\?v=)|youtu\\.be/)([A-Za-z0-9_-]{11})(?:[?&].*)?$");

    @Transactional(readOnly = true)
    public List<PostResponse> list(String symbol, Long viewerId, int size) {
        if (coins.findBySymbolIgnoreCaseAndActiveTrue(symbol).isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다.");
        return posts.findByCoinSymbolIgnoreCaseOrderByCreatedAtDesc(symbol, PageRequest.of(0, Math.min(Math.max(size, 1), 100)))
            .stream().map(post -> response(post, viewerId)).toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> feed(Long viewerId, int size) {
        return posts.findAllByOrderByCreatedAtDesc(PageRequest.of(0, bounded(size))).stream().map(post -> response(post, viewerId)).toList();
    }

    @Transactional(readOnly = true)
    public PostResponse get(Long postId, Long viewerId) { return response(post(postId), viewerId); }

    @Transactional
    public PostResponse create(Long memberId, CreatePostRequest request) {
        Member member = member(memberId);
        Coin coin = coins.findBySymbolIgnoreCaseAndActiveTrue(request.coinSymbol()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다."));
        AssetSnapshot snapshot = assets.snapshotForPost(memberId, coin.getId());
        boolean verified = snapshot != null && snapshot.isVerified();
        BigDecimal value = verified ? snapshot.getValueKrw() : null;
        YoutubeData youtube = youtube(request.youtubeUrl());
        BigDecimal price = request.assetPrice();
        if ((price == null) != (request.assetPriceCurrency() == null))
            throw new ApiException(HttpStatus.BAD_REQUEST, "자산 가격과 통화는 함께 입력해야 합니다.");
        Post saved = posts.save(new Post(member, coin, request.title().trim(), request.content().trim(), value, verified,
            clean(request.tradingViewSymbol()), clean(request.tradingViewInterval()), clean(request.tradingViewAnalysis()),
            price, request.assetPriceCurrency(), price == null ? null : Instant.now(),
            youtube == null ? null : youtube.url(), youtube == null ? null : youtube.videoId(), youtube == null ? null : youtube.thumbnailUrl()));
        if (request.media() != null) {
            for (int index = 0; index < request.media().size(); index++) {
                MediaRequest item = request.media().get(index);
                if (item.type() == null) throw new ApiException(HttpStatus.BAD_REQUEST, "미디어 형식이 필요합니다.");
                validateMediaUrl(item.url());
                if (item.thumbnailUrl() != null) validateMediaUrl(item.thumbnailUrl());
                media.save(new PostMedia(saved, item.type(), item.url(), item.thumbnailUrl(), index));
            }
        }
        return response(saved, memberId);
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

    @Transactional
    public PostResponse bookmark(Long memberId, Long postId) {
        Post post = post(postId); PostMemberId id = new PostMemberId(postId, memberId);
        if (!bookmarks.existsById(id)) bookmarks.save(new PostBookmark(post, member(memberId)));
        return response(post, memberId);
    }

    @Transactional
    public PostResponse unbookmark(Long memberId, Long postId) {
        bookmarks.deleteById(new PostMemberId(postId, memberId)); return response(post(postId), memberId);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> bookmarks(Long memberId) {
        member(memberId);
        return bookmarks.findByMemberIdOrderByCreatedAtDesc(memberId).stream().map(item -> response(item.getPost(), memberId)).toList();
    }

    @Transactional
    public PostResponse repost(Long memberId, Long postId) {
        Post post = post(postId); PostMemberId id = new PostMemberId(postId, memberId);
        if (!reposts.existsById(id)) reposts.save(new PostRepost(post, member(memberId)));
        return response(post, memberId);
    }

    @Transactional
    public PostResponse unrepost(Long memberId, Long postId) {
        reposts.deleteById(new PostMemberId(postId, memberId)); return response(post(postId), memberId);
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
        boolean reposted = viewerId != null && reposts.existsById(new PostMemberId(post.getId(), viewerId));
        boolean bookmarked = viewerId != null && bookmarks.existsById(new PostMemberId(post.getId(), viewerId));
        List<MediaResponse> mediaResponses = media.findByPostIdOrderByDisplayOrder(post.getId()).stream()
            .map(item -> new MediaResponse(item.getId(), item.getMediaType().name(), item.getUrl(), item.getThumbnailUrl(), item.getDisplayOrder())).toList();
        TradingViewResponse tradingView = post.getTradingViewSymbol() == null ? null :
            new TradingViewResponse(post.getTradingViewSymbol(), post.getTradingViewInterval(), post.getTradingViewAnalysis());
        PriceSnapshotResponse priceSnapshot = post.getAssetPrice() == null ? null :
            new PriceSnapshotResponse(post.getAssetPrice(), post.getAssetPriceCurrency(), post.getAssetPriceAt());
        YoutubeResponse youtube = post.getYoutubeVideoId() == null ? null :
            new YoutubeResponse(post.getYoutubeUrl(), post.getYoutubeVideoId(), post.getYoutubeThumbnailUrl());
        return new PostResponse(post.getId(), post.getCoin().getSymbol(), post.getTitle(), post.getContent(),
            new AuthorResponse(author.getId(), author.getNickname(), author.getAvatarColor(), address),
            visible && post.isAuthorVerified(), value, display, likes.countByPostId(post.getId()), comments.countByPostId(post.getId()), liked, post.getCreatedAt(),
            mediaResponses, tradingView, priceSnapshot, youtube, reposts.countByPostId(post.getId()), reposted, bookmarked);
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
    private int bounded(int size) { return Math.min(Math.max(size, 1), 100); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private void validateMediaUrl(String value) {
        if (value.startsWith("/api/v1/media/")) return;
        try {
            URI uri = URI.create(value);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "미디어 URL은 업로드 URL 또는 HTTPS URL이어야 합니다.");
        }
    }
    private YoutubeData youtube(String value) {
        if (value == null || value.isBlank()) return null;
        Matcher matcher = YOUTUBE.matcher(value.trim());
        if (!matcher.matches()) throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 YouTube 또는 Shorts URL이 아닙니다.");
        String videoId = matcher.group(1);
        return new YoutubeData(value.trim(), videoId, "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg");
    }
    private record YoutubeData(String url, String videoId, String thumbnailUrl) {}
}
