"use client";

import { FormEvent, useMemo, useState } from "react";

type Coin = { symbol: string; name: string; price: string; change: string; color: string; members: string; holders: string; description: string; };
type Post = { id: number; name: string; handle: string; initials: string; time: string; title: string; body: string; asset: string; verified: boolean; likes: number; comments: number; tag?: string; liked?: boolean; };

const coins: Coin[] = [
  { symbol: "BTC", name: "Bitcoin", price: "₩158.2M", change: "+2.84%", color: "#f7931a", members: "128K", holders: "82%", description: "비트코인 시장, 온체인 흐름과 장기 투자 이야기를 나누는 공간" },
  { symbol: "ETH", name: "Ethereum", price: "₩5.42M", change: "+4.12%", color: "#627eea", members: "94K", holders: "76%", description: "이더리움 생태계와 시장에 대해 가장 빠르게 이야기하는 공간" },
  { symbol: "SOL", name: "Solana", price: "₩248K", change: "+6.31%", color: "#7c5cff", members: "61K", holders: "69%", description: "솔라나 생태계, 밈코인과 디파이 정보를 나누는 공간" },
  { symbol: "XRP", name: "XRP", price: "₩3,218", change: "-0.72%", color: "#23292f", members: "48K", holders: "74%", description: "XRP와 글로벌 결제 시장에 대한 투자자 커뮤니티" },
  { symbol: "DOGE", name: "Dogecoin", price: "₩292", change: "+1.19%", color: "#c9a633", members: "42K", holders: "58%", description: "도지코인 홀더들의 유쾌하고 솔직한 투자 이야기" },
];

const starterPosts: Post[] = [
  { id: 1, name: "체인위의고래", handle: "0x7A2F...91C4", initials: "CW", time: "8분 전", title: "이번 조정에서 ETH를 조금 더 담았습니다", body: "ETF 순유입과 스테이킹 물량을 같이 보면 단기 변동성보다 공급 감소 쪽이 더 중요해 보여요. 3,600달러 부근은 여전히 분할 매수 구간으로 보고 있습니다.", asset: "₩342,840,000", verified: true, likes: 248, comments: 37, tag: "매매일지" },
  { id: 2, name: "레이어투러버", handle: "0x3B8D...4E21", initials: "L2", time: "24분 전", title: "다음 업그레이드가 L2 수수료에 미치는 영향 정리", body: "블롭 처리량 확대가 예정대로 반영되면 사용자가 체감하는 수수료는 한 단계 더 낮아질 수 있습니다. 다만 시퀀서 수익 구조 변화는 프로젝트별로 확인이 필요해요.", asset: "₩87,120,000", verified: true, likes: 182, comments: 29, tag: "리서치" },
  { id: 3, name: "블록산책", handle: "0x91CC...08AF", initials: "BS", time: "41분 전", title: "스테이킹 처음 시작할 때 체크할 것들", body: "수익률 숫자만 보지 말고 출금 대기 시간, 운영 주체, 스마트 컨트랙트 리스크를 함께 보세요. 처음이라면 소액으로 직접 흐름을 경험해 보는 걸 추천합니다.", asset: "자산 비공개", verified: false, likes: 96, comments: 18, tag: "정보" },
];

const trending = [["1", "ETH 현물 ETF", "1,284 posts"], ["2", "Pectra 업그레이드", "896 posts"], ["3", "스테이킹", "642 posts"], ["4", "레이어 2", "418 posts"]];

export default function Home() {
  const [activeCoin, setActiveCoin] = useState(coins[1]);
  const [posts, setPosts] = useState(starterPosts);
  const [filter, setFilter] = useState("인기");
  const [connected, setConnected] = useState(false);
  const [walletOpen, setWalletOpen] = useState(false);
  const [composerOpen, setComposerOpen] = useState(false);
  const [search, setSearch] = useState("");
  const filteredCoins = useMemo(() => coins.filter((coin) => `${coin.name} ${coin.symbol}`.toLowerCase().includes(search.toLowerCase())), [search]);

  const selectCoin = (coin: Coin) => {
    setActiveCoin(coin);
    setPosts(starterPosts.map((post) => ({ ...post, id: post.id + coins.indexOf(coin) * 10 })));
  };
  const openComposer = () => connected ? setComposerOpen(true) : setWalletOpen(true);
  const connectWallet = () => { setConnected(true); setWalletOpen(false); };
  const createPost = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const title = String(form.get("title") || "").trim();
    const body = String(form.get("body") || "").trim();
    if (!title || !body) return;
    setPosts((current) => [{ id: Date.now(), name: "크립토노트", handle: "0x18D7...A93E", initials: "CN", time: "방금 전", title, body, asset: "₩52,480,000", verified: true, likes: 0, comments: 0, tag: "의견" }, ...current]);
    setComposerOpen(false);
  };
  const toggleLike = (id: number) => setPosts((current) => current.map((post) => post.id === id ? { ...post, liked: !post.liked, likes: post.likes + (post.liked ? -1 : 1) } : post));

  return (
    <main className="app-shell">
      <header className="topbar">
        <button className="brand" onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })} aria-label="Cryptalk 홈"><span className="brand-mark">C</span><span>CRYPTALK</span></button>
        <div className="top-search"><span>⌕</span><input aria-label="전체 검색" placeholder="코인, 게시글, 지갑 주소 검색" /><kbd>⌘ K</kbd></div>
        <nav className="top-actions" aria-label="주요 메뉴">
          <button className="icon-button" aria-label="알림">♢<span className="notification-dot" /></button>
          {connected ? <button className="wallet-connected" onClick={() => setConnected(false)}><span className="status-dot" />0x18D7...A93E <span className="chevron">⌄</span></button> : <button className="connect-button" onClick={() => setWalletOpen(true)}>지갑 연결</button>}
        </nav>
      </header>

      <div className="layout">
        <aside className="sidebar">
          <div className="sidebar-head"><p>커뮤니티</p><button aria-label="커뮤니티 추가">＋</button></div>
          <label className="coin-search"><span>⌕</span><input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="코인 찾기" /></label>
          <div className="coin-list">
            {filteredCoins.map((coin) => (
              <button key={coin.symbol} className={`coin-row ${activeCoin.symbol === coin.symbol ? "active" : ""}`} onClick={() => selectCoin(coin)}>
                <span className="coin-logo" style={{ background: coin.color }}>{coin.symbol.slice(0, 1)}</span>
                <span className="coin-name"><strong>{coin.name}</strong><small>{coin.symbol}</small></span>
                <span className="coin-price"><strong>{coin.price}</strong><small className={coin.change.startsWith("-") ? "down" : "up"}>{coin.change}</small></span>
              </button>
            ))}
          </div>
          <div className="sidebar-foot"><button><span>◈</span> 전체 피드</button><button><span>☆</span> 북마크</button><button><span>◎</span> 내 활동</button></div>
        </aside>

        <section className="feed">
          <div className="community-hero">
            <div className="hero-main"><span className="hero-logo" style={{ background: activeCoin.color }}>{activeCoin.symbol.slice(0, 1)}</span><div><div className="eyebrow">{activeCoin.symbol} COMMUNITY</div><h1>{activeCoin.name}</h1><p>{activeCoin.description}</p></div></div>
            <div className="community-stats"><div><strong>{activeCoin.members}</strong><span>멤버</span></div><i /><div><strong>{activeCoin.holders}</strong><span>보유 인증률</span></div><button className="write-button" onClick={openComposer}>＋ 글쓰기</button></div>
          </div>
          <div className="feed-controls"><div className="tabs">{["인기", "최신", "보유 인증"].map((item) => <button key={item} className={filter === item ? "active" : ""} onClick={() => setFilter(item)}>{item}</button>)}</div><button className="sort-button">24시간 <span>⌄</span></button></div>
          {!connected && <button className="wallet-nudge" onClick={() => setWalletOpen(true)}><span className="nudge-icon">◈</span><span><strong>지갑을 연결하고 홀더 인증을 시작하세요</strong><small>보유 자산을 공개하고 신뢰도 높은 대화에 참여할 수 있어요.</small></span><b>연결하기 →</b></button>}
          <div className="post-list">
            {posts.filter((post) => filter !== "보유 인증" || post.verified).map((post) => (
              <article className="post-card" key={post.id}>
                <div className="post-author"><span className="avatar">{post.initials}</span><div className="author-copy"><div className="name-line"><strong>{post.name}</strong>{post.verified && <span className="verified" title="보유 인증 완료">✓</span>}<span>· {post.time}</span></div><div className="asset-line"><code>{post.handle}</code><span className={post.verified ? "asset verified-asset" : "asset"}>{post.verified && "◆ "}{post.asset}</span></div></div><button className="more-button" aria-label="더보기">•••</button></div>
                <div className="post-content"><div className="post-meta"><span className="post-tag">{post.tag}</span><span>{activeCoin.symbol}</span></div><h2>{post.title}</h2><p>{post.body}</p></div>
                <div className="post-actions"><button className={post.liked ? "liked" : ""} onClick={() => toggleLike(post.id)} aria-label="좋아요">{post.liked ? "♥" : "♡"} <span>{post.likes}</span></button><button aria-label="댓글">▢ <span>{post.comments}</span></button><button aria-label="공유">↗</button><button className="save" aria-label="저장">♧</button></div>
              </article>
            ))}
          </div>
        </section>

        <aside className="rightbar">
          <section className="my-assets"><div className="section-title"><span>내 자산</span><button>•••</button></div>{connected ? <><p className="total-label">총 보유 자산</p><strong className="total-asset">₩52,480,000</strong><span className="daily-up">+₩1,284,200 (2.51%)</span><div className="mini-assets"><div><span className="mini-logo eth">E</span><p><strong>ETH</strong><small>9.68 ETH</small></p><b>₩52.4M</b></div></div></> : <div className="locked-assets"><span>◇</span><strong>자산을 불러올까요?</strong><p>지갑 연결 후 보유 자산을<br />안전하게 확인할 수 있어요.</p><button onClick={() => setWalletOpen(true)}>지갑 연결하기</button></div>}</section>
          <section className="trending-card"><div className="section-title"><span>지금 뜨는 토픽</span><small>LIVE</small></div>{trending.map(([rank, topic, count]) => <button className="trend-row" key={rank}><b>{rank}</b><span><strong>{topic}</strong><small>{count}</small></span><i>↗</i></button>)}</section>
          <section className="trust-card"><span className="trust-mark">✓</span><div><strong>보유 인증이란?</strong><p>지갑 서명으로 자산 보유를 증명해요. 주소와 정확한 수량 공개 여부는 직접 선택할 수 있습니다.</p><button>자세히 알아보기 →</button></div></section>
          <footer><a href="#">이용약관</a><a href="#">개인정보처리방침</a><a href="#">가이드</a><span>© 2026 CRYPTALK</span></footer>
        </aside>
      </div>

      {walletOpen && <div className="modal-backdrop" onMouseDown={() => setWalletOpen(false)}><section className="modal wallet-modal" onMouseDown={(e) => e.stopPropagation()} role="dialog" aria-modal="true" aria-labelledby="wallet-title"><button className="modal-close" onClick={() => setWalletOpen(false)}>×</button><span className="modal-symbol">◇</span><h2 id="wallet-title">지갑 연결</h2><p>지갑을 연결해 홀더 전용 대화에 참여하세요.<br />연결만으로는 거래가 발생하지 않습니다.</p><div className="wallet-options"><button onClick={connectWallet}><span className="wallet-icon fox">M</span><strong>MetaMask</strong><small>가장 인기 있는 지갑</small><b>→</b></button><button onClick={connectWallet}><span className="wallet-icon wc">W</span><strong>WalletConnect</strong><small>모바일 지갑 연결</small><b>→</b></button><button onClick={connectWallet}><span className="wallet-icon cb">C</span><strong>Coinbase Wallet</strong><small>Coinbase 계정 연결</small><b>→</b></button></div><small className="terms-copy">연결 시 이용약관 및 개인정보처리방침에 동의하게 됩니다.</small></section></div>}
      {composerOpen && <div className="modal-backdrop" onMouseDown={() => setComposerOpen(false)}><form className="modal composer" onSubmit={createPost} onMouseDown={(e) => e.stopPropagation()}><div className="composer-head"><div><span className="hero-logo small" style={{ background: activeCoin.color }}>{activeCoin.symbol.slice(0, 1)}</span><span><strong>{activeCoin.name}</strong><small>커뮤니티에 글쓰기</small></span></div><button type="button" onClick={() => setComposerOpen(false)}>×</button></div><input name="title" aria-label="글 제목" placeholder="제목을 입력하세요" maxLength={80} autoFocus required /><textarea name="body" aria-label="글 내용" placeholder="투자 아이디어와 정보를 자유롭게 공유해 보세요." maxLength={600} required /><div className="identity-preview"><span className="avatar">CN</span><span><strong>크립토노트 <i className="verified">✓</i></strong><small>◆ 보유 자산 ₩52,480,000 공개</small></span><button type="button">공개 설정</button></div><div className="composer-actions"><div><button type="button">#</button><button type="button">▧</button><button type="button">☺</button></div><button className="submit-post" type="submit">게시하기</button></div></form></div>}
    </main>
  );
}
