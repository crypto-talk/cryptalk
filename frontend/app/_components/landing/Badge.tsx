import { BADGE, type Tier } from "../../../lib/mock/landing";

/** 인증 등급 배지. 지갑연결 / 거래소연동 / 미인증 3단계입니다. */
export default function Badge({ tier }: { tier: Tier }) {
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
