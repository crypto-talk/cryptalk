import type { TrendingRoom } from "../types";

/**
 * 1·2·3위 카드 배색. 디자인 아트보드의 PODIUM_STYLE 그대로다.
 * 여기서만 쓰므로 목데이터가 아니라 이 컴포넌트가 들고 있는다.
 *
 * ⚠️ 2단계 ④에서 styles/tokens.css 의 토큰으로 바꾼다.
 */
const PODIUM_STYLE = [
  { bg: "#6E56F0", ink: "#FFFFFF", subInk: "#FFFFFF", rankColor: "#FFFFFF", arrowColor: "#FFFFFF" },
  { bg: "#CCCCFF", ink: "#1A1A1F", subInk: "#1A1A1F", rankColor: "#6E56F0", arrowColor: "#6E56F0" },
  { bg: "#F6F6F8", ink: "#1A1A1F", subInk: "#6B6B75", rankColor: "#6E56F0", arrowColor: "#6E56F0" },
];

type Props = {
  rooms: TrendingRoom[];
};

const meta = (room: TrendingRoom) => `글 ${room.posts} · 댓글 ${room.comments}`;

export default function TrendingRooms({ rooms }: Props) {
  const podium = rooms.slice(0, 3);
  const rest = rooms.slice(3);

  return (
    <section>
      <div className="hd-section-head">
        <h2 className="hd-t-h1">지금 뜨는 방</h2>
        <span className="hd-info" title="글·댓글 수 기준이며 시세와 무관합니다">
          i
        </span>
        <div style={{ flex: 1 }} />
        <a href="#" className="hd-t-sm hd-strong hd-only-mobile">
          전체 방
        </a>
      </div>

      <div className="hd-podium">
        {podium.map((room, index) => {
          const style = PODIUM_STYLE[index];
          return (
            <div
              key={room.symbol}
              className="hd-podium-card"
              style={{ background: style.bg, animationDelay: `${index * 0.12}s` }}
            >
              <a href="#" style={{ display: "block", color: style.ink }}>
                <div className="hd-t-display" style={{ color: style.rankColor }}>
                  {room.rank}
                </div>
                <div
                  style={{
                    marginTop: 8,
                    display: "flex",
                    alignItems: "baseline",
                    gap: 8,
                  }}
                >
                  <span className="hd-t-h1" style={{ color: style.ink }}>
                    {room.symbol}
                  </span>
                  <span className="hd-t-body" style={{ color: style.subInk }}>
                    {room.name}
                  </span>
                  {room.hot ? (
                    <span
                      className="hd-t-body"
                      style={{ color: style.arrowColor }}
                      aria-label="상승 중"
                    >
                      ▲
                    </span>
                  ) : null}
                </div>
                <div className="hd-t-sm hd-num" style={{ marginTop: 8, color: style.subInk }}>
                  {meta(room)}
                </div>
              </a>
              <a
                href="#"
                className="hd-t-sm"
                style={{ display: "block", marginTop: 16, color: style.subInk }}
              >
                {room.latest}
              </a>
            </div>
          );
        })}
      </div>

      <div className="hd-rest">
        {rest.map((room) => (
          <div key={room.symbol} className="hd-rest-row">
            <a href="#" className="hd-rest-line">
              <span className="hd-t-sm hd-muted hd-num" style={{ width: 24, flex: "0 0 auto" }}>
                {room.rank}
              </span>
              <span className="hd-t-body hd-strong" style={{ flex: "0 0 auto" }}>
                {room.symbol}
              </span>
              {room.hot ? (
                <span
                  className="hd-t-body"
                  style={{ color: "var(--hd-purple)", flex: "0 0 auto" }}
                  aria-label="상승 중"
                >
                  ▲
                </span>
              ) : null}
              <span className="hd-t-sm hd-muted hd-ellipsis" style={{ flex: 1, minWidth: 0 }}>
                {room.name}
              </span>
              <span className="hd-t-sm hd-muted hd-num" style={{ flex: "0 0 auto" }}>
                {meta(room)}
              </span>
            </a>
            <a href="#" className="hd-rest-latest">
              {room.latest}
            </a>
          </div>
        ))}
      </div>
    </section>
  );
}
