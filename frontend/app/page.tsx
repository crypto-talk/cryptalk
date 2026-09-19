"use client";

import { useCallback, useEffect, useState } from "react";
import { loadFeed, loadRooms } from "@/features/landing/api";
import { loadWallets, type ConnectedWallet } from "@/features/wallet/api";
import { api, linkInjectedWallet, refreshSession, type Member } from "@/lib/api";
import {
  TICKER,
  TRENDING,
  VOTES,
  type FeedPost,
  type HotPost,
  type Room,
} from "@/lib/mock/landing";
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

/**
 * 호들잇 랜딩.
 *
 * 백엔드에서 오는 것
 *   - 세션 복원 · 회원가입 · 로그인 · 로그아웃
 *   - 지갑 연결과 연결된 지갑 목록  (POST /me/wallet + GET /me/wallets)
 *   - 사이드바 방 목록과 24h 등락률  (GET /coins + /market/prices)
 *   - 전체 글과 핫글                 (GET /feed)
 *
 * 아직 목데이터인 것 — 백엔드에 API 자체가 없습니다
 *   - 티커 숫자, 지금 뜨는 방(G-3 미합의), 오늘의 투표(G-5)
 *
 * 불러오기에 실패하면 목데이터로 가리지 않고 화면에 알립니다. 배포본에서
 * 백엔드가 안 붙은 것을 바로 알아야 하기 때문입니다.
 */
export default function Landing() {
  const [member, setMember] = useState<Member | null>(null);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [posts, setPosts] = useState<FeedPost[]>([]);
  const [hot, setHot] = useState<HotPost[]>([]);
  const [wallets, setWallets] = useState<ConnectedWallet[]>([]);
  const [authMode, setAuthMode] = useState<AuthMode | null>(null);
  const [notice, setNotice] = useState("");
  const [loadFailed, setLoadFailed] = useState(false);

  useEffect(() => {
    refreshSession()
      .then(setMember)
      .catch(() => undefined);

    // 방 목록과 피드는 서로 막지 않게 따로 부릅니다. 한쪽이 실패해도 다른 쪽은 뜹니다.
    loadRooms()
      .then(setRooms)
      .catch(() => setLoadFailed(true));

    loadFeed()
      .then((feed) => {
        setPosts(feed.posts);
        setHot(feed.hot);
      })
      .catch(() => setLoadFailed(true));
  }, []);

  /**
   * 연결된 지갑 목록. 로그인 상태에서만 부를 수 있습니다.
   *
   * member.walletAddress 하나로 판단하지 않는 이유는, 백엔드가 한 회원에 여러
   * 지갑을 붙일 수 있고 자산도 합산해서 계산하기 때문입니다.
   */
  const refreshWallets = useCallback(() => {
    loadWallets()
      .then(setWallets)
      .catch(() => setWallets([]));
  }, []);

  useEffect(() => {
    // 로그아웃 때 비우는 것은 logout() 이 합니다. effect 안에서 바로 setState 하지 않습니다.
    if (!member) return;
    refreshWallets();
  }, [member, refreshWallets]);

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
      // member 가 새 객체로 바뀌면 위 effect 가 지갑 목록을 다시 불러옵니다.
      setMember(await linkInjectedWallet());
    } catch (reason) {
      setNotice(reason instanceof Error ? reason.message : "지갑 연결에 실패했습니다.");
    }
  };

  const logout = async () => {
    await api.logout().catch(() => undefined);
    setMember(null);
    setWallets([]);
  };

  return (
    <div className="hd">
      <Header
        member={member}
        onLogin={() => setAuthMode("login")}
        onSignup={() => setAuthMode("signup")}
        onLogout={logout}
        onWrite={() => requireLogin("글쓰기 화면은 아직 준비 중입니다.")}
      />

      <Ticker items={TICKER} />

      <div className="hd-body">
        <Sidebar rooms={rooms} wallets={wallets} onConnectWallet={connectWallet} />

        <div className="hd-main">
          {loadFailed ? (
            <p role="status" className="hd-t-sm" style={{ color: "var(--hd-down)" }}>
              백엔드에서 데이터를 불러오지 못했습니다. API 주소와 CORS 설정을 확인해 주세요.
            </p>
          ) : null}

          {notice ? (
            <p role="status" className="hd-t-sm" style={{ color: "var(--hd-down)" }}>
              {notice}
            </p>
          ) : null}

          <TrendingRooms rooms={TRENDING} />
          <VotePanel votes={VOTES} onVote={() => requireLogin("투표는 아직 준비 중입니다.")} />
          <HotPosts posts={hot} />
          <PostFeed posts={posts} />
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
