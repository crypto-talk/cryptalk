/**
 * 랜딩 목데이터와 뷰 모델 타입.
 *
 * ⚠️ 여기 남은 값이 랜딩의 가짜 데이터 전부입니다.
 *
 * 방 목록·전체 글·핫글은 실제 API로 옮겼습니다(`features/landing/api.ts`).
 * 남은 것은 백엔드에 API 자체가 없는 세 가지뿐입니다.
 *   - TICKER    랜딩 집계 API 없음
 *   - TRENDING  방별 24h 집계 없음 (G-3 미합의)
 *   - VOTES     일일 투표 기능 자체가 없음 (G-5)
 *
 * 타입은 아직 여기 둡니다. 랜딩이 features/landing/ 으로 옮겨갈 때 같이 갑니다.
 */

// ── 타입 ────────────────────────────────────────────────────────────────

/** 인증 등급. 백엔드 `verifiedHolder` + 향후 거래소 연동 여부로 결정됩니다. */
export type Tier = "wallet" | "exchange" | "none";

export type BadgeStyle = {
  label: string;
  bg: string;
  color: string;
  border: string;
};

export type Room = {
  symbol: string;
  name: string;
  /** 24h 등락률. `GET /market/prices`의 change24h에서 옵니다. 없으면 `-`. */
  change: string;
  current?: boolean;
};

export type TrendingRoom = {
  rank: number;
  symbol: string;
  name: string;
  posts: number;
  comments: number;
  hot: boolean;
  latest: string;
};

export type VoteRow = {
  symbol: string;
  allWidth: string;
  allLabel: string;
  holderWidth: string;
  holderLabel: string;
};

export type HotPost = {
  rank: number;
  symbol: string;
  tier: Tier;
  title: string;
  meta: string;
};

export type FeedPost = {
  id: number;
  symbol: string;
  tier: Tier;
  /** 수량 구간. **서버가 완성해서 주는 문자열**입니다. 프론트가 계산하지 않습니다. */
  range: string;
  time: string;
  title: string;
  preview: string;
  nick: string;
  /** 보유 기간 문구. features/badge가 만듭니다. 인덱서 전까지 항상 `보유 기간 미확인`. */
  hold: string;
  comments: number;
  /** ⚠️ 조회수 API가 없습니다. null이면 화면에서 자리를 뺍니다. */
  views: number | null;
};

export type TickerItem = {
  lead?: string;
  text: string;
  value?: string;
};

// ── 배지 ────────────────────────────────────────────────────────────────

export const BADGE: Record<Tier, BadgeStyle> = {
  wallet: { label: "지갑연결", bg: "#6E56F0", color: "#FFFFFF", border: "transparent" },
  exchange: { label: "거래소연동", bg: "#CCCCFF", color: "#1A1A1F", border: "transparent" },
  none: { label: "미인증", bg: "transparent", color: "#6B6B75", border: "#E6E6EB" },
};

// ── 코인 한글명 ─────────────────────────────────────────────────────────

/**
 * `GET /api/v1/coins`는 영문명만 돌려줍니다(`Bitcoin`).
 * 디자인은 한글명을 쓰므로 프론트에서 매핑합니다.
 * 백엔드에 한글명 필드가 생기면 이 표는 지웁니다.
 */
export const COIN_NAME_KO: Record<string, string> = {
  BTC: "비트코인",
  ETH: "이더리움",
  SOL: "솔라나",
  XRP: "리플",
  DOGE: "도지코인",
  ADA: "카르다노",
  BNB: "BNB",
  AVAX: "아발란체",
  DOT: "폴카닷",
  LINK: "체인링크",
  POL: "폴리곤",
  TON: "톤코인",
  TRX: "트론",
  LTC: "라이트코인",
  BCH: "비트코인 캐시",
  UNI: "유니스왑",
  AAVE: "에이브",
  ATOM: "코스모스",
  NEAR: "니어",
  SUI: "수이",
  ARB: "아비트럼",
  OP: "옵티미즘",
};

export const coinNameKo = (symbol: string, fallback: string) => COIN_NAME_KO[symbol] ?? fallback;

// ── 티커 ────────────────────────────────────────────────────────────────

/** 대기 중인 API: 랜딩 집계 (`GET /landing`) */
export const TICKER: TickerItem[] = [
  {
    lead: "지갑에 이름표를 붙인 커뮤니티",
    text: " — 글쓴이가 그 코인을 실제로 들고 있는지 보입니다",
  },
  { text: "오늘 올라온 글 ", value: "340" },
  { text: "평균 보유 기간이 가장 긴 방 ", value: "BTC 1년 4개월" },
  { text: "지갑 연결한 사용자 ", value: "128명" },
];

// ── 지금 뜨는 방 ────────────────────────────────────────────────────────

/** 대기 중인 API: 방별 24h 글·댓글 집계 */
export const TRENDING: TrendingRoom[] = [
  {
    rank: 1,
    symbol: "ETH",
    name: "이더리움",
    posts: 47,
    comments: 210,
    hot: true,
    latest: "덴쿤 이후 L2 수수료, 실제 체감은 어느 정도인가요",
  },
  {
    rank: 2,
    symbol: "BTC",
    name: "비트코인",
    posts: 39,
    comments: 174,
    hot: true,
    latest: "현물 ETF 순유입이 3주 연속인데 이번엔 뭐가 다른가",
  },
  {
    rank: 3,
    symbol: "SOL",
    name: "솔라나",
    posts: 31,
    comments: 96,
    hot: false,
    latest: "솔라나 밸리데이터 직접 돌려본 3개월 후기",
  },
  {
    rank: 4,
    symbol: "XRP",
    name: "리플",
    posts: 22,
    comments: 88,
    hot: true,
    latest: "2년 3개월 들고 있으면서 배운 것 몇 가지",
  },
  {
    rank: 5,
    symbol: "ARB",
    name: "아비트럼",
    posts: 18,
    comments: 74,
    hot: false,
    latest: "에어드랍 이후 남은 사람들은 무엇을 보고 있나",
  },
  {
    rank: 6,
    symbol: "DOGE",
    name: "도지코인",
    posts: 14,
    comments: 61,
    hot: true,
    latest: "밈코인 방에도 보유 배지가 필요한 이유",
  },
  {
    rank: 7,
    symbol: "LINK",
    name: "체인링크",
    posts: 9,
    comments: 38,
    hot: false,
    latest: "오라클 수수료 구조를 처음부터 다시 읽어봤습니다",
  },
  {
    rank: 8,
    symbol: "ADA",
    name: "카르다노",
    posts: 6,
    comments: 27,
    hot: false,
    latest: "거버넌스 투표 참여율이 낮은 이유가 뭘까요",
  },
  {
    rank: 9,
    symbol: "AVAX",
    name: "아발란체",
    posts: 5,
    comments: 19,
    hot: false,
    latest: "서브넷 운영비 실측치 공유합니다",
  },
  {
    rank: 10,
    symbol: "OP",
    name: "옵티미즘",
    posts: 4,
    comments: 12,
    hot: false,
    latest: "리트로 펀딩 라운드 결과를 어떻게 봐야 하나",
  },
];

/** 1·2·3위 카드 배색. 디자인 아트보드의 PODIUM_STYLE 그대로입니다. */
export const PODIUM_STYLE = [
  { bg: "#6E56F0", ink: "#FFFFFF", subInk: "#FFFFFF", rankColor: "#FFFFFF", arrowColor: "#FFFFFF" },
  { bg: "#CCCCFF", ink: "#1A1A1F", subInk: "#1A1A1F", rankColor: "#6E56F0", arrowColor: "#6E56F0" },
  { bg: "#F6F6F8", ink: "#1A1A1F", subInk: "#6B6B75", rankColor: "#6E56F0", arrowColor: "#6E56F0" },
];

// ── 오늘의 투표 ─────────────────────────────────────────────────────────

/** 대기 중인 API: 일일 투표. 기능 자체가 백엔드에 없습니다. */
export const VOTES: VoteRow[] = [
  {
    symbol: "ETH",
    allWidth: "68%",
    allLabel: "68% (312명)",
    holderWidth: "41%",
    holderLabel: "41% (68명)",
  },
  {
    symbol: "BTC",
    allWidth: "54%",
    allLabel: "54% (204명)",
    holderWidth: "61%",
    holderLabel: "61% (51명)",
  },
  {
    symbol: "SOL",
    allWidth: "71%",
    allLabel: "71% (96명)",
    holderWidth: "33%",
    holderLabel: "9명 중 3명",
  },
];
