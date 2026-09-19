/**
 * 인증 등급.
 *
 * 지금 백엔드가 주는 것은 `verifiedHolder` 불리언뿐이다. 거래소 연동은
 * 이번 단계에서 제외했으므로(G-5) `exchange` 는 타입에만 두고 화면에서
 * 만들지 않는다. 연동이 생기면 여기 한 곳만 고친다.
 */
export type Tier = "wallet" | "exchange" | "none";

export const TIER_LABEL: Record<Tier, string> = {
  wallet: "지갑연결",
  exchange: "거래소연동",
  none: "미인증",
};

export function tierLabel(tier: Tier): string {
  return TIER_LABEL[tier];
}

/** 백엔드의 `verifiedHolder` 를 등급으로 옮긴다. */
export function tierOf(verifiedHolder: boolean): Tier {
  return verifiedHolder ? "wallet" : "none";
}
