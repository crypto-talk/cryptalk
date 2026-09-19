import type { Room } from "../../../lib/mock/landing";
import Logo from "./Logo";

type Props = {
  rooms: Room[];
  /** 지갑 연결 여부. member.walletAddress로 판단합니다. */
  walletLinked: boolean;
  onConnectWallet: () => void;
};

const changeColor = (change: string) => {
  if (change.startsWith("-")) return "var(--hd-down)";
  if (change.startsWith("+")) return "var(--hd-up)";
  return "var(--hd-sub)";
};

export default function Sidebar({ rooms, walletLinked, onConnectWallet }: Props) {
  return (
    <div className="hd-sidebar">
      <div className="hd-label">내 코인</div>
      <div className="hd-card hd-t-sm hd-muted" style={{ marginTop: 8 }}>
        {walletLinked
          ? "보유 중인 코인의 방이 여기 고정됩니다"
          : "지갑을 연결하면 보유 중인 코인의 방이 여기 고정됩니다"}
      </div>

      <div style={{ marginTop: 32, display: "flex", alignItems: "baseline", gap: 8 }}>
        <div className="hd-label" style={{ flex: 1 }}>
          전체 방
        </div>
        <div className="hd-t-xs hd-muted">24h</div>
      </div>

      <div style={{ marginTop: 8, display: "flex", flexDirection: "column" }}>
        {rooms.map((room) => (
          <a
            key={room.symbol}
            href="#"
            className={`hd-room${room.current ? " hd-room-current" : ""}`}
          >
            <span className="hd-t-sm hd-strong" style={{ flex: "0 0 auto" }}>
              {room.symbol}
            </span>
            <span className="hd-t-sm hd-muted hd-ellipsis" style={{ flex: 1, minWidth: 0 }}>
              {room.name}
            </span>
            <span
              className="hd-t-sm hd-num"
              style={{ flex: "0 0 auto", color: changeColor(room.change) }}
            >
              {room.change}
            </span>
          </a>
        ))}
      </div>

      <div className="hd-card" style={{ marginTop: 32, textAlign: "center" }}>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <Logo size={56} background="#CCCCFF" foreground="#1A1A1F" />
        </div>
        <div className="hd-t-h2" style={{ marginTop: 16 }}>
          지갑을 연결하면
        </div>
        <div className="hd-cta-list">
          {[
            "글에 보유 배지가 붙습니다",
            "보유 기간이 기록으로 쌓입니다",
            "내 코인 방이 고정됩니다",
          ].map((line) => (
            <div key={line} className="hd-cta-item">
              <span className="hd-dot" />
              <span className="hd-t-sm hd-muted">{line}</span>
            </div>
          ))}
        </div>
        <button
          type="button"
          className="hd-btn hd-btn-primary hd-btn-block"
          style={{ marginTop: 16 }}
          onClick={onConnectWallet}
          disabled={walletLinked}
        >
          {walletLinked ? "연결됨" : "지갑 연결"}
        </button>
        <div className="hd-t-xs hd-muted" style={{ marginTop: 8 }}>
          연결 안 해도 읽고 쓸 수 있습니다
        </div>
      </div>

      <div className="hd-sidebar-links">
        <a href="#">커뮤니티 규칙</a>
        <a href="#">의견 보내기</a>
        <a href="#">광고 문의</a>
      </div>
    </div>
  );
}
