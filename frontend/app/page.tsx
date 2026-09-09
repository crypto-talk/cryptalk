"use client";

import { useCallback, useEffect, useState } from "react";
import { api, linkInjectedWallet, refreshSession, type Member } from "../lib/api";
import {
  HOT,
  POSTS,
  ROOMS,
  TICKER,
  TRENDING,
  VOTES,
  coinNameKo,
  mockChangeFor,
  type Room,
} from "../lib/mock/landing";
import AuthDialog, { type AuthMode } from "./_components/auth/AuthDialog";
import Footer from "./_components/landing/Footer";
import Header from "./_components/landing/Header";
import HotPosts from "./_components/landing/HotPosts";
import PostFeed from "./_components/landing/PostFeed";
import Sidebar from "./_components/landing/Sidebar";
import Ticker from "./_components/landing/Ticker";
import TrendingRooms from "./_components/landing/TrendingRooms";
import VotePanel from "./_components/landing/VotePanel";
import "./_components/landing/landing.css";

const PRETENDARD =
  "https://cdn.jsdelivr.net/gh/orioncactus/pretendard@1.3.9/dist/web/variable/pretendardvariable.css";

/**
 * 호들잇 랜딩 — 디자인 아트보드 기반 목업.
 *
 * 실제로 백엔드에 붙는 것은 두 가지뿐입니다.
 *   1. 로그인·회원가입·세션 복원·로그아웃
 *   2. 사이드바 '전체 방' 목록 (GET /api/v1/coins)
 *
 * 나머지 섹션(티커·뜨는 방·투표·핫글·전체 글)은 전부 lib/mock/landing.ts의
 * 목데이터입니다. 어떤 API를 기다리는지는 `claude/랜딩_데이터_매핑.md` 참조.
 */
export default function Landing() {
  const [member, setMember] = useState<Member | null>(null);
  const [rooms, setRooms] = useState<Room[]>(ROOMS);
  const [authMode, setAuthMode] = useState<AuthMode | null>(null);
  const [notice, setNotice] = useState("");

  useEffect(() => {
    refreshSession().then(setMember).catch(() => undefined);

    // 방 목록만 실제 API로 채웁니다. 등락률은 응답에 없어 목값을 씁니다.
    api
      .coins()
      .then((coins) =>
        setRooms(
          coins.slice(0, 10).map((coin) => ({
            symbol: coin.symbol,
            name: coinNameKo(coin.symbol, coin.name),
            change: mockChangeFor(coin.symbol),
            // 왼쪽 보라 마크. 디자인 재현용이며 실제 '현재 방' 상태는 아직 없습니다.
            current: coin.symbol === "ETH",
          })),
        ),
      )
      .catch(() => {
        // 백엔드에 닿지 않으면 목 목록을 그대로 둡니다. 화면은 깨지지 않습니다.
      });
  }, []);

  const requireLogin = useCallback(
    (message: string) => {
      if (member) {
        setNotice(message);
        return false;
      }
      setAuthMode("login");
      return true;
    },
    [member],
  );

  const connectWallet = async () => {
    if (requireLogin("")) return;
    setNotice("");
    try {
      setMember(await linkInjectedWallet());
    } catch (reason) {
      setNotice(reason instanceof Error ? reason.message : "지갑 연결에 실패했습니다.");
    }
  };

  const logout = async () => {
    await api.logout().catch(() => undefined);
    setMember(null);
  };

  return (
    <div className="hd">
      {/* React 19가 <link>를 head로 올려줘서 layout.tsx를 건드리지 않아도 됩니다. */}
      <link rel="stylesheet" href={PRETENDARD} precedence="default" />

      <Header
        member={member}
        onLogin={() => setAuthMode("login")}
        onSignup={() => setAuthMode("signup")}
        onLogout={logout}
        onWrite={() => requireLogin("글쓰기 화면은 아직 준비 중입니다.")}
      />

      <Ticker items={TICKER} />

      <div className="hd-body">
        <Sidebar
          rooms={rooms}
          walletLinked={Boolean(member?.walletAddress)}
          onConnectWallet={connectWallet}
        />

        <div className="hd-main">
          {notice ? (
            <p role="status" className="hd-t-sm" style={{ color: "var(--hd-down)" }}>
              {notice}
            </p>
          ) : null}

          <TrendingRooms rooms={TRENDING} />
          <VotePanel votes={VOTES} onVote={() => requireLogin("투표는 아직 준비 중입니다.")} />
          <HotPosts posts={HOT} />
          <PostFeed posts={POSTS} />
        </div>
      </div>

      <Footer />

      <div className="hd-fab">
        <button
          type="button"
          className="hd-btn hd-btn-primary"
          onClick={() => requireLogin("글쓰기 화면은 아직 준비 중입니다.")}
        >
          글쓰기
        </button>
      </div>

      {authMode ? (
        <AuthDialog
          mode={authMode}
          onModeChange={setAuthMode}
          onClose={() => setAuthMode(null)}
          onAuthenticated={(next) => {
            setMember(next);
            setAuthMode(null);
          }}
        />
      ) : null}
    </div>
  );
}
