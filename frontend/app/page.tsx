"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import { api, ApiCoin, ApiPost, Asset, linkInjectedWallet, MarketPrice, Member, refreshSession } from "../lib/api";

type Coin = { symbol: string; name: string; price: string; change: string; color: string; members: string; holders: string; description: string; };
type Post = { id: number; name: string; handle: string; initials: string; time: string; title: string; body: string; asset: string; verified: boolean; likes: number; comments: number; tag?: string; liked?: boolean; };
type AuthMode = "login" | "signup" | "link";

const coins: Coin[] = [
  { symbol: "BTC", name: "Bitcoin", price: "—", change: "시세 조회 중", color: "#f7931a", members: "128K", holders: "82%", description: "비트코인 시장, 온체인 흐름과 장기 투자 이야기를 나누는 공간" },
  { symbol: "ETH", name: "Ethereum", price: "—", change: "시세 조회 중", color: "#627eea", members: "94K", holders: "76%", description: "이더리움 생태계와 시장에 대해 가장 빠르게 이야기하는 공간" },
  { symbol: "SOL", name: "Solana", price: "—", change: "시세 조회 중", color: "#7c5cff", members: "61K", holders: "69%", description: "솔라나 생태계, 밈코인과 디파이 정보를 나누는 공간" },
  { symbol: "XRP", name: "XRP", price: "—", change: "시세 조회 중", color: "#23292f", members: "48K", holders: "74%", description: "XRP와 글로벌 결제 시장에 대한 투자자 커뮤니티" },
  { symbol: "DOGE", name: "Dogecoin", price: "—", change: "시세 조회 중", color: "#c9a633", members: "42K", holders: "58%", description: "도지코인 홀더들의 유쾌하고 솔직한 투자 이야기" },
];

const trending = [["1", "ETH 현물 ETF", "1,284 posts"], ["2", "Pectra 업그레이드", "896 posts"], ["3", "스테이킹", "642 posts"], ["4", "레이어 2", "418 posts"]];
const COIN_PREVIEW_COUNT = 5;

type CoinMeta = Pick<Coin, "price" | "change" | "members" | "holders" | "description">;
const coinMeta: Record<string, CoinMeta> = Object.fromEntries(coins.map((coin) => [coin.symbol, coin]));
const defaultMeta: CoinMeta = { price: "—", change: "시세 조회 중", members: "—", holders: "—", description: "실시간 시세와 투자 이야기를 나누는 커뮤니티" };
const toCoin = (coin: ApiCoin): Coin => ({ ...defaultMeta, ...coinMeta[coin.symbol], symbol: coin.symbol, name: coin.name, color: coin.accentColor });
const formatPrice = (price: number) => new Intl.NumberFormat("ko-KR", {
  style: "currency", currency: "KRW", maximumFractionDigits: price >= 100 ? 0 : price >= 1 ? 2 : 4,
}).format(price);
const withMarketPrices = (items: Coin[], prices: MarketPrice[]) => {
  const bySymbol = new Map(prices.map((price) => [price.symbol, price]));
  return items.map((coin) => {
    const market = bySymbol.get(coin.symbol);
    if (!market) return coin;
    const change = market.change24h;
    return { ...coin, price: formatPrice(market.price), change: change == null ? "등락률 미제공" : `${change >= 0 ? "+" : ""}${change.toFixed(2)}%` };
  });
};
const toPost = (post: ApiPost): Post => ({
  id: post.id, name: post.author.nickname, handle: post.author.walletAddress ?? "일반 회원", initials: post.author.nickname.slice(0, 2).toUpperCase(),
  time: new Intl.RelativeTimeFormat("ko", { numeric: "auto" }).format(-Math.max(1, Math.floor((Date.now() - new Date(post.createdAt).getTime()) / 60000)), "minute"),
  title: post.title, body: post.content, asset: post.assetDisplay, verified: post.verifiedHolder,
  likes: post.likes, comments: post.comments, tag: "의견", liked: post.liked,
});

export default function Home() {
  const [activeCoin, setActiveCoin] = useState(coins[1]);
  const [communityCoins, setCommunityCoins] = useState(coins);
  const [posts, setPosts] = useState<Post[]>([]);
  const [filter, setFilter] = useState("인기");
  const [member, setMember] = useState<Member | null>(null);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [error, setError] = useState("");
  const [walletOpen, setWalletOpen] = useState(false);
  const [authMode, setAuthMode] = useState<AuthMode>("login");
  const [composerOpen, setComposerOpen] = useState(false);
  const [mobileSearchOpen, setMobileSearchOpen] = useState(false);
  const [search, setSearch] = useState("");
  const [showAllCoins, setShowAllCoins] = useState(false);
  const connected = member !== null;
  const walletLinked = Boolean(member?.walletAddress);
  const filteredCoins = useMemo(() => communityCoins.filter((coin) => `${coin.name} ${coin.symbol}`.toLowerCase().includes(search.toLowerCase())), [search, communityCoins]);
  const visibleCoins = search || showAllCoins ? filteredCoins : filteredCoins.slice(0, COIN_PREVIEW_COUNT);

  useEffect(() => {
    let cancelled = false;
    const refreshPrices = () => api.marketPrices().then((prices) => {
      if (cancelled) return;
      setCommunityCoins((items) => withMarketPrices(items, prices));
      setActiveCoin((coin) => withMarketPrices([coin], prices)[0]);
    }).catch(() => undefined);
    api.coins().then((items) => {
      if (cancelled) return;
      const loaded = items.map(toCoin);
      setCommunityCoins(loaded);
      setActiveCoin(loaded.find((coin) => coin.symbol === "ETH") ?? loaded[0]);
      refreshPrices();
    }).catch(() => setError("백엔드 서버에 연결할 수 없습니다."));
    refreshSession().then((profile) => { setMember(profile); if (profile) api.assets().then(setAssets).catch(() => undefined); });
    const interval = window.setInterval(refreshPrices, 30_000);
    return () => { cancelled = true; window.clearInterval(interval); };
  }, []);

  useEffect(() => {
    api.posts(activeCoin.symbol).then((items) => setPosts(items.map(toPost))).catch(() => setPosts([]));
  }, [activeCoin.symbol, member]);

  const selectCoin = (coin: Coin) => {
    setActiveCoin(coin);
  };
  const openAuth = (mode: AuthMode = "login") => { setError(""); setAuthMode(mode); setWalletOpen(true); };
  const openComposer = () => connected ? setComposerOpen(true) : openAuth("login");
  const connectWallet = async () => {
    setError("");
    try {
      const profile = await linkInjectedWallet();
      setMember(profile); setWalletOpen(false); setAssets(await api.assets());
    }
    catch (reason) { setError(reason instanceof Error ? reason.message : "지갑 연결에 실패했습니다."); }
  };
  const authenticate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setError("");
    const form = new FormData(event.currentTarget);
    try {
      const profile = authMode === "signup"
        ? await api.signup(String(form.get("loginId")), String(form.get("password")), String(form.get("nickname")))
        : await api.login(String(form.get("loginId")), String(form.get("password")));
      setMember(profile); setAssets([]); setWalletOpen(false);
    } catch (reason) { setError(reason instanceof Error ? reason.message : "인증에 실패했습니다."); }
  };
  const disconnectWallet = async () => { await api.logout(); setMember(null); setAssets([]); };
  const createPost = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const title = String(form.get("title") || "").trim();
    const body = String(form.get("body") || "").trim();
    if (!title || !body) return;
    try { const created = await api.createPost(activeCoin.symbol, title, body); setPosts((current) => [toPost(created), ...current]); setComposerOpen(false); }
    catch (reason) { setError(reason instanceof Error ? reason.message : "글을 등록하지 못했습니다."); }
  };
  const toggleLike = async (id: number) => {
    if (!connected) return openAuth("login");
    const current = posts.find((post) => post.id === id); if (!current) return;
    try { const updated = toPost(await api.like(id, Boolean(current.liked))); setPosts((items) => items.map((post) => post.id === id ? updated : post)); }
    catch (reason) { setError(reason instanceof Error ? reason.message : "좋아요를 처리하지 못했습니다."); }
  };

  return (
    <main className="app-shell">
      <header className="topbar">
        <button className="brand" onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })} aria-label="Cryptalk 홈"><span className="brand-mark">C</span><span>CRYPTALK</span></button>
        <div className="top-search"><span>⌕</span><input aria-label="전체 검색" placeholder="코인, 게시글, 지갑 주소 검색" /><kbd>⌘ K</kbd></div>
        <nav className="top-actions" aria-label="주요 메뉴">
          <button className="icon-button" aria-label="알림">♢<span className="notification-dot" /></button>
          {connected ? <button className="wallet-connected" onClick={disconnectWallet}><span className="status-dot" />{member.walletAddress ? `${member.walletAddress.slice(0, 6)}...${member.walletAddress.slice(-4)}` : member.nickname} <span className="chevron">⌄</span></button> : <button className="connect-button" onClick={() => openAuth("login")}>로그인</button>}
        </nav>
      </header>

      <div className="layout">
        <aside className="sidebar">
          <div className="sidebar-head"><p>커뮤니티</p><button aria-label="커뮤니티 추가">＋</button></div>
          <label className="coin-search"><span>⌕</span><input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="코인 찾기" /></label>
          <div className="coin-list">
            {visibleCoins.map((coin) => (
              <button key={coin.symbol} className={`coin-row ${activeCoin.symbol === coin.symbol ? "active" : ""}`} onClick={() => selectCoin(coin)}>
                <span className="coin-logo" style={{ background: coin.color }}>{coin.symbol.slice(0, 1)}</span>
                <span className="coin-name"><strong>{coin.name}</strong><small>{coin.symbol}</small></span>
                <span className="coin-price"><strong>{coin.price}</strong><small className={coin.change.startsWith("-") ? "down" : "up"}>{coin.change}</small></span>
              </button>
            ))}
          </div>
          {!search && filteredCoins.length > COIN_PREVIEW_COUNT && (
            <button className="coin-more" onClick={() => setShowAllCoins((open) => !open)}>
              {showAllCoins ? "접기" : `더보기 (${filteredCoins.length - COIN_PREVIEW_COUNT})`}
            </button>
          )}
          <div className="sidebar-foot"><button><span>◈</span> 전체 피드</button><button><span>☆</span> 북마크</button><button><span>◎</span> 내 활동</button></div>
        </aside>

        <section className="feed">
          {mobileSearchOpen && <label className="mobile-search"><span>⌕</span><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="코인 또는 심볼 검색" autoFocus /><button type="button" onClick={() => setMobileSearchOpen(false)} aria-label="검색 닫기">×</button></label>}
          <div className="community-hero">
            <div className="hero-main"><span className="hero-logo" style={{ background: activeCoin.color }}>{activeCoin.symbol.slice(0, 1)}</span><div><div className="eyebrow">{activeCoin.symbol} COMMUNITY</div><h1>{activeCoin.name}</h1><p>{activeCoin.description}</p><p className="hero-price"><strong>{activeCoin.price}</strong><span className={activeCoin.change.startsWith("-") ? "down" : "up"}>{activeCoin.change}</span><small>24시간</small></p></div></div>
            <div className="community-stats"><div><strong>{activeCoin.members}</strong><span>멤버</span></div><i /><div><strong>{activeCoin.holders}</strong><span>보유 인증률</span></div><button className="write-button" onClick={openComposer}>＋ 글쓰기</button></div>
          </div>
          <div className="feed-controls"><div className="tabs">{["인기", "최신", "보유 인증"].map((item) => <button key={item} className={filter === item ? "active" : ""} onClick={() => setFilter(item)}>{item}</button>)}</div><button className="sort-button">24시간 <span>⌄</span></button></div>
          {!connected && <button className="wallet-nudge" onClick={() => openAuth("signup")}><span className="nudge-icon">◎</span><span><strong>회원가입하고 커뮤니티에 참여하세요</strong><small>지갑은 가입 후 선택적으로 연결해 보유 자산을 인증할 수 있어요.</small></span><b>가입하기 →</b></button>}
          {connected && !walletLinked && <button className="wallet-nudge" onClick={() => openAuth("link")}><span className="nudge-icon">◈</span><span><strong>지갑을 연결하고 홀더 인증을 시작하세요</strong><small>계정은 유지하면서 지갑 소유권과 보유 자산을 인증합니다.</small></span><b>연결하기 →</b></button>}
          <section className="mobile-assets" id="mobile-assets" aria-label="내 자산 요약">
            <div><span>내 자산</span>{connected && <small>블록체인 조회 기준</small>}</div>
            {connected && walletLinked ? <><strong>₩{assets.reduce((sum, asset) => sum + Number(asset.valueKrw), 0).toLocaleString("ko-KR")}</strong><div className="mobile-asset-coins">{assets.map((asset) => <span key={asset.symbol}>{asset.symbol} {Number(asset.quantity).toLocaleString()}</span>)}</div></> : <button type="button" onClick={() => openAuth(connected ? "link" : "login")}>{connected ? "지갑을 연결해 자산 인증" : "로그인하고 자산 확인"}</button>}
          </section>
          {error && <p role="alert" style={{ color: "#dc2626", padding: "12px 20px", margin: 0 }}>{error}</p>}
          <div className="post-list">
            {posts.filter((post) => filter !== "보유 인증" || post.verified).map((post) => (
              <article className="post-card" key={post.id}>
                <div className="post-author"><span className="avatar">{post.initials}</span><div className="author-copy"><div className="name-line"><strong>{post.name}</strong>{post.verified && <span className="verified" title="보유 인증 완료">✓</span>}<span>· {post.time}</span></div><div className="asset-line"><code>{post.handle}</code><span className={post.verified ? "asset verified-asset" : "asset"}>{post.verified && "◆ "}{post.asset}</span></div></div><button className="more-button" aria-label="더보기">•••</button></div>
                <div className="post-content"><div className="post-meta"><span className="post-tag">{post.tag}</span><span>{activeCoin.symbol}</span></div><h2>{post.title}</h2><p>{post.body}</p></div>
                <div className="post-actions"><button className={post.liked ? "liked" : ""} onClick={() => toggleLike(post.id)} aria-label="좋아요">{post.liked ? "♥" : "♡"} <span>{post.likes}</span></button><button aria-label="댓글">▢ <span>{post.comments}</span></button><button aria-label="공유">↗</button><button className="save" aria-label="저장">♧</button></div>
              </article>
            ))}
            {posts.length === 0 && <div style={{ padding: 48, textAlign: "center", color: "#7b8190" }}>아직 작성된 글이 없습니다. 첫 글을 남겨보세요.</div>}
          </div>
        </section>

        <aside className="rightbar">
          <section className="my-assets"><div className="section-title"><span>내 자산</span><button>•••</button></div>{connected && walletLinked ? <><p className="total-label">총 보유 자산</p><strong className="total-asset">₩{assets.reduce((sum, asset) => sum + Number(asset.valueKrw), 0).toLocaleString("ko-KR")}</strong><span className="daily-up">블록체인 조회 기준</span><div className="mini-assets">{assets.map((asset) => <div key={asset.symbol}><span className="mini-logo eth">{asset.symbol.slice(0, 1)}</span><p><strong>{asset.symbol}</strong><small>{Number(asset.quantity).toLocaleString()} {asset.symbol}</small></p><b>₩{Number(asset.valueKrw).toLocaleString("ko-KR")}</b></div>)}</div></> : <div className="locked-assets"><span>◇</span><strong>{connected ? "지갑을 연결할까요?" : "자산을 불러올까요?"}</strong><p>{connected ? "계정에 지갑을 연결하고 보유 자산을 인증하세요." : <>로그인 후 지갑을 연결해 보유 자산을<br />안전하게 확인할 수 있어요.</>}</p><button onClick={() => openAuth(connected ? "link" : "login")}>{connected ? "지갑 연결하기" : "로그인하기"}</button></div>}</section>
          <section className="trending-card"><div className="section-title"><span>지금 뜨는 토픽</span><small>LIVE</small></div>{trending.map(([rank, topic, count]) => <button className="trend-row" key={rank}><b>{rank}</b><span><strong>{topic}</strong><small>{count}</small></span><i>↗</i></button>)}</section>
          <section className="trust-card"><span className="trust-mark">✓</span><div><strong>보유 인증이란?</strong><p>지갑 서명으로 자산 보유를 증명해요. 주소와 정확한 수량 공개 여부는 직접 선택할 수 있습니다.</p><button>자세히 알아보기 →</button></div></section>
          <footer><a href="#">이용약관</a><a href="#">개인정보처리방침</a><a href="#">가이드</a><span>© 2026 CRYPTALK</span></footer>
        </aside>
      </div>

      <nav className="mobile-nav" aria-label="모바일 주요 메뉴">
        <button type="button" onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}><span>⌂</span>홈</button>
        <button type="button" className={mobileSearchOpen ? "active" : ""} onClick={() => setMobileSearchOpen((open) => !open)}><span>⌕</span>검색</button>
        <button type="button" className="mobile-write" onClick={openComposer} aria-label="글쓰기"><span>＋</span></button>
        <button type="button" onClick={() => document.getElementById("mobile-assets")?.scrollIntoView({ behavior: "smooth", block: "center" })}><span>◇</span>자산</button>
        <button type="button" onClick={() => connected ? disconnectWallet() : openAuth("login")}><span>◎</span>{connected ? "로그아웃" : "로그인"}</button>
      </nav>

      {walletOpen && <div className="modal-backdrop" onMouseDown={() => setWalletOpen(false)}><section className="modal wallet-modal auth-modal" onMouseDown={(e) => e.stopPropagation()} role="dialog" aria-modal="true" aria-labelledby="auth-title"><button className="modal-close" onClick={() => setWalletOpen(false)}>×</button><span className="modal-symbol">{authMode === "link" ? "◇" : "◎"}</span><h2 id="auth-title">{authMode === "signup" ? "회원가입" : authMode === "login" ? "로그인" : "지갑 자산 인증"}</h2><p>{authMode === "link" ? "서명으로 지갑 소유권을 확인하고 자산 인증을 시작합니다." : "일반 계정으로 참여하고 지갑은 필요할 때 연결하세요."}</p>{error && <p role="alert" className="auth-error">{error}</p>}{authMode === "login" || authMode === "signup" ? <form className="auth-form" onSubmit={authenticate}>{authMode === "signup" && <input name="nickname" aria-label="닉네임" placeholder="닉네임" minLength={2} maxLength={40} required />}<input name="loginId" aria-label="아이디" placeholder="아이디 (4~40자, 영문·숫자·._-)" minLength={4} maxLength={40} pattern="[A-Za-z0-9._-]+" autoComplete="username" required /><input name="password" type="password" aria-label="비밀번호" placeholder="비밀번호 (8자 이상)" minLength={8} maxLength={72} autoComplete={authMode === "signup" ? "new-password" : "current-password"} required /><button type="submit">{authMode === "signup" ? "계정 만들기" : "로그인"}</button></form> : <div className="wallet-options"><button onClick={connectWallet}><span className="wallet-icon fox">M</span><strong>EVM 지갑</strong><small>MetaMask · Coinbase Wallet</small><b>→</b></button></div>}<div className="auth-switch">{authMode === "login" && <button type="button" onClick={() => setAuthMode("signup")}>회원가입</button>}{authMode === "signup" && <button type="button" onClick={() => setAuthMode("login")}>이미 계정이 있어요</button>}</div><small className="terms-copy">지갑 서명에는 거래나 수수료가 발생하지 않습니다.</small></section></div>}
      {composerOpen && member && <div className="modal-backdrop" onMouseDown={() => setComposerOpen(false)}><form className="modal composer" onSubmit={createPost} onMouseDown={(e) => e.stopPropagation()}><div className="composer-head"><div><span className="hero-logo small" style={{ background: activeCoin.color }}>{activeCoin.symbol.slice(0, 1)}</span><span><strong>{activeCoin.name}</strong><small>커뮤니티에 글쓰기</small></span></div><button type="button" onClick={() => setComposerOpen(false)}>×</button></div><input name="title" aria-label="글 제목" placeholder="제목을 입력하세요" maxLength={80} autoFocus required /><textarea name="body" aria-label="글 내용" placeholder="투자 아이디어와 정보를 자유롭게 공유해 보세요." maxLength={600} required /><div className="identity-preview"><span className="avatar">{member.nickname.slice(0, 2).toUpperCase()}</span><span><strong>{member.nickname}</strong><small>{assets.find((asset) => asset.symbol === activeCoin.symbol)?.verified ? `◆ 보유 자산 ₩${Number(assets.find((asset) => asset.symbol === activeCoin.symbol)?.valueKrw).toLocaleString("ko-KR")} 공개` : "보유 인증 정보 없음"}</small></span><button type="button">{member.assetVisibility}</button></div><div className="composer-actions"><div><button type="button">#</button><button type="button">▧</button><button type="button">☺</button></div><button className="submit-post" type="submit">게시하기</button></div></form></div>}
    </main>
  );
}
