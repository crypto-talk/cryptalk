import type { Tier } from "../types";

type BadgeStyle = {
  label: string;
  bg: string;
  color: string;
  border: string;
};

/**
 * 인증 등급 배지. 지갑연결 / 거래소연동 / 미인증 3단계.
 *
 * ⚠️ 2단계 ④에서 styles/tokens.css 의 토큰으로 바꾼다. 지금은 아트보드 값을
 * 그대로 들고 있다.
 */
const BADGE: Record<Tier, BadgeStyle> = {
  wallet: { label: "지갑연결", bg: "#6E56F0", color: "#FFFFFF", border: "transparent" },
  exchange: { label: "거래소연동", bg: "#CCCCFF", color: "#1A1A1F", border: "transparent" },
  none: { label: "미인증", bg: "transparent", color: "#6B6B75", border: "#E6E6EB" },
};

export default function TierBadge({ tier }: { tier: Tier }) {
  const style = BADGE[tier];
  return (
    <span
      className="hd-badge"
      style={{ background: style.bg, color: style.color, borderColor: style.border }}
    >
      {style.label}
    </span>
  );
}
