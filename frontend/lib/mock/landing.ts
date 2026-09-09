/**
 * 랜딩 목데이터.
 *
 * ⚠️ 이 파일 하나가 랜딩의 가짜 데이터 전부입니다.
 * 백엔드 API가 준비되면 이 파일의 각 export를 fetch로 바꾸면 확장이 끝나도록,
 * 섹션 컴포넌트는 여기서 나온 값을 props로만 받습니다.
 *
 * 각 항목이 어떤 API를 기다리는지는 `claude/랜딩_데이터_매핑.md` 1장 참조.
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
  /** 24h 등락률. ⚠️ 현재 API에 없는 값입니다. */
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
  /** 수량 구간. 지갑 특정 방지 규칙(C-1)에 따라 넓은 구간만 씁니다. */
  range: string;
  time: string;
  title: string;
  preview: string;
  nick: string;
  /** ⚠️ holdingMonths가 항상 null이라 지금은 만들 수 없는 값입니다. */
  hold: string;
  comments: number;
  views: number;
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

// ── 사이드바 방 목록 ────────────────────────────────────────────────────

/** `GET /api/v1/coins` 실패 시 폴백. 등락률은 어느 경우든 목데이터입니다. */
export const ROOMS: Room[] = [
  { symbol: "BTC", name: "비트코인", change: "+2.4%" },
  { symbol: "ETH", name: "이더리움", change: "+4.1%", current: true },
  { symbol: "SOL", name: "솔라나", change: "-1.8%" },
  { symbol: "XRP", name: "리플", change: "+0.6%" },
  { symbol: "ARB", name: "아비트럼", change: "-3.2%" },
  { symbol: "DOGE", name: "도지코인", change: "+7.9%" },
  { symbol: "LINK", name: "체인링크", change: "-0.4%" },
  { symbol: "ADA", name: "카르다노", change: "+1.1%" },
  { symbol: "AVAX", name: "아발란체", change: "-2.0%" },
  { symbol: "OP", name: "옵티미즘", change: "+0.9%" },
];

/** 심볼만 실제 API에서 오고 등락률은 목값을 돌려씁니다. */
export const mockChangeFor = (symbol: string) =>
  ROOMS.find((room) => room.symbol === symbol)?.change ?? "—";

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

// ── 오늘의 핫글 ─────────────────────────────────────────────────────────

/** 대기 중인 API: 조회수 컬럼 + 정렬 */
export const HOT: HotPost[] = [
  {
    rank: 1,
    symbol: "XRP",
    tier: "wallet",
    title: "2년 3개월 들고 있으면서 배운 것 몇 가지",
    meta: "조회 2,880 · 댓글 51",
  },
  {
    rank: 2,
    symbol: "ETH",
    tier: "wallet",
    title: "덴쿤 이후 L2 수수료, 실제 체감은 어느 정도인가요",
    meta: "조회 1,204 · 댓글 24",
  },
  {
    rank: 3,
    symbol: "BTC",
    tier: "exchange",
    title: "현물 ETF 순유입이 3주 연속인데 이번엔 뭐가 다른가",
    meta: "조회 890 · 댓글 9",
  },
  {
    rank: 4,
    symbol: "SOL",
    tier: "none",
    title: "솔라나 밸리데이터 직접 돌려본 3개월 후기",
    meta: "조회 640 · 댓글 17",
  },
  {
    rank: 5,
    symbol: "ARB",
    tier: "wallet",
    title: "에어드랍 이후 남은 사람들은 무엇을 보고 있나",
    meta: "조회 310 · 댓글 8",
  },
];

// ── 전체 글 ─────────────────────────────────────────────────────────────

/** 대기 중인 API: 전체 피드. 지금은 `/communities/{symbol}/posts`뿐이라 코인별입니다. */
export const POSTS: FeedPost[] = [
  {
    id: 1,
    symbol: "ETH",
    tier: "wallet",
    range: "10~100 ETH",
    time: "12분 전",
    title: "덴쿤 이후 L2 수수료, 실제 체감은 어느 정도인가요",
    preview:
      "지난주부터 아비트럼이랑 베이스에서 스왑을 스무 번쯤 돌려봤습니다. 체감상 가스비가 예전 절반 아래로 떨어졌는데, 다른 분들 기록도 궁금합니다.",
    nick: "닉네임_01",
    hold: "ETH 8개월 보유",
    comments: 24,
    views: 1204,
  },
  {
    id: 2,
    symbol: "BTC",
    tier: "exchange",
    range: "1~10 BTC",
    time: "31분 전",
    title: "현물 ETF 순유입이 3주 연속인데 이번엔 뭐가 다른가",
    preview:
      "예전 랠리 때와 매수 주체 구성이 다르다는 얘기가 많습니다. 기관 비중이 실제로 얼마나 늘었는지 자료 정리해봤습니다.",
    nick: "닉네임_02",
    hold: "보유 기간 미확인",
    comments: 9,
    views: 412,
  },
  {
    id: 3,
    symbol: "SOL",
    tier: "none",
    range: "",
    time: "44분 전",
    title: "솔라나 밸리데이터 직접 돌려본 3개월 후기",
    preview:
      "하드웨어 비용, 다운타임, 실제 수익까지 계산해서 정리했습니다. 생각보다 손이 많이 갑니다.",
    nick: "닉네임_03",
    hold: "",
    comments: 3,
    views: 88,
  },
  {
    id: 4,
    symbol: "XRP",
    tier: "wallet",
    range: "1000~10000 XRP",
    time: "1시간 전",
    title: "2년 3개월 들고 있으면서 배운 것 몇 가지",
    preview:
      "소송 기간 내내 팔지 않았습니다. 잘한 판단이었는지는 아직 모르겠지만, 그동안 지켰던 원칙은 공유할 만한 것 같습니다.",
    nick: "닉네임_04",
    hold: "XRP 2년 3개월 보유",
    comments: 51,
    views: 2880,
  },
  {
    id: 5,
    symbol: "DOGE",
    tier: "exchange",
    range: "10000~100000 DOGE",
    time: "1시간 전",
    title: "밈코인 방에도 보유 배지가 필요한 이유",
    preview:
      "말만 하고 안 들고 있는 사람이 제일 시끄럽습니다. 배지가 붙고 나서 방 분위기가 어떻게 바뀌었는지 적어봤습니다.",
    nick: "닉네임_05",
    hold: "보유 기간 미확인",
    comments: 17,
    views: 640,
  },
];
