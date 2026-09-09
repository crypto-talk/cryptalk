"use client";

import { useState } from "react";
import type { HotPost } from "../../../lib/mock/landing";
import Badge from "./Badge";

type Props = {
  posts: HotPost[];
};

export default function HotPosts({ posts }: Props) {
  const [verifiedOnly, setVerifiedOnly] = useState(false);
  const visible = verifiedOnly ? posts.filter((post) => post.tier !== "none") : posts;

  return (
    <section>
      <div className="hd-section-head">
        <h2 className="hd-t-h2">오늘의 핫글</h2>
        <span className="hd-info" title="조회수 기준으로 정렬합니다">
          i
        </span>
      </div>

      <div className="hd-tabs" role="tablist">
        <button
          type="button"
          role="tab"
          aria-selected={!verifiedOnly}
          className={`hd-tab${verifiedOnly ? "" : " hd-tab-on"}`}
          onClick={() => setVerifiedOnly(false)}
        >
          핫글
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={verifiedOnly}
          className={`hd-tab${verifiedOnly ? " hd-tab-on" : ""}`}
          onClick={() => setVerifiedOnly(true)}
        >
          인증 핫글
        </button>
      </div>

      <div className="hd-hot-list">
        {visible.map((post) => (
          <a key={post.rank} href="#" className="hd-hot-row">
            <span
              className="hd-t-sm hd-strong hd-muted hd-num"
              style={{ width: 24, textAlign: "center", flex: "0 0 auto" }}
            >
              {post.rank}
            </span>
            <span className="hd-chip">{post.symbol}</span>
            <Badge tier={post.tier} />
            <span className="hd-t-body hd-ellipsis" style={{ flex: "1 1 240px", minWidth: 0 }}>
              {post.title}
            </span>
            <span className="hd-t-xs hd-muted hd-num">{post.meta}</span>
          </a>
        ))}
        {visible.length === 0 ? <div className="hd-empty">아직 인증 핫글이 없습니다.</div> : null}
      </div>
    </section>
  );
}
