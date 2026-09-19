import { clearAccessToken, setAccessToken } from "@/lib/auth-token";
import { http } from "@/lib/http";

/**
 * 구조 개편 전부터 있던 API 모음. 현재 랜딩(`app/page.tsx`)과 `AuthDialog` 가 쓴다.
 *
 * 전송은 `lib/http.ts` 로 넘겼다. 토큰 저장과 401 갱신을 여기서 한 번 더 구현하고
 * 있었고, 저장소가 둘로 갈라지면 한쪽만 갱신되는 상태가 생긴다.
 *
 * 타입은 아직 손으로 적은 것을 쓴다. `lib/api-schema.ts` 의 생성 타입은 응답
 * 필드가 전부 선택(`?`)이라 지금 갈아끼우면 랜딩 컴포넌트를 함께 고쳐야 한다.
 * 백엔드가 응답 스키마에 required 를 채우면 그때 `features/<domain>/types.ts` 로 옮긴다.
 *
 * 이 파일은 화면이 `features/<domain>/api.ts` 로 옮겨가면서 사라진다. 새 코드를
 * 여기에 추가하지 않는다.
 */

export type ApiCoin = {
  id: number;
  symbol: string;
  name: string;
  chainType: string;
  accentColor: string;
};

export type MarketPrice = {
  symbol: string;
  price: number;
  currency: string;
  change24h: number | null;
  capturedAt: string;
  source: string;
};

export type Member = {
  id: number;
  nickname: string;
  avatarColor: string;
  walletAddress: string | null;
  assetVisibility: string;
};

export type Asset = {
  symbol: string;
  quantity: number;
  valueKrw: number;
  quantityBand: string | null;
  verified: boolean;
  verificationLevel: string;
  status: string;
  walletCount: number;
  holdingSince: string | null;
  holdingMonths: number | null;
  capturedAt: string;
  blockNumber: number | null;
  syncStatus: string;
};

export type AssetPortfolio = {
  walletCount: number;
  assets: Asset[];
};

export type ApiPost = {
  id: number;
  coinSymbol: string;
  title: string;
  content: string;
  author: { id: number; nickname: string; avatarColor: string; walletAddress: string | null };
  verifiedHolder: boolean;
  assetValueKrw?: number;
  assetDisplay: string;
  likes: number;
  comments: number;
  liked: boolean;
  createdAt: string;
};

type AuthResult = { accessToken: string; member: Member };

async function authenticate(path: string, body: unknown): Promise<Member> {
  const result = await http<AuthResult>(path, { method: "POST", body: JSON.stringify(body) });
  setAccessToken(result.accessToken);
  return result.member;
}

/**
 * refresh 쿠키로 세션을 복원한다. 실패는 "로그인 안 된 상태"라 오류가 아니므로
 * null 을 돌려준다.
 */
export async function refreshSession(): Promise<Member | null> {
  try {
    return await authenticate("/api/v1/auth/refresh", undefined);
  } catch {
    clearAccessToken();
    return null;
  }
}

type InjectedProvider = {
  request(args: { method: string; params?: unknown[] }): Promise<unknown>;
};

async function injectedWallet() {
  const ethereum = (window as typeof window & { ethereum?: InjectedProvider }).ethereum;
  if (!ethereum) throw new Error("EVM 지갑 확장 프로그램을 설치해 주세요.");

  const accounts = (await ethereum.request({ method: "eth_requestAccounts" })) as string[];
  const walletAddress = accounts[0];
  if (!walletAddress) throw new Error("지갑 계정을 선택해 주세요.");

  return { ethereum, walletAddress };
}

export async function linkInjectedWallet(): Promise<Member> {
  const { ethereum, walletAddress } = await injectedWallet();

  const nonce = await http<{ nonceId: string; message: string }>("/api/v1/me/wallet/nonce", {
    method: "POST",
    body: JSON.stringify({ walletAddress }),
  });

  const signature = (await ethereum.request({
    method: "personal_sign",
    params: [nonce.message, walletAddress],
  })) as string;

  return http<Member>("/api/v1/me/wallet", {
    method: "POST",
    body: JSON.stringify({ walletAddress, nonceId: nonce.nonceId, signature }),
  });
}

export const api = {
  signup: (loginId: string, password: string, nickname: string) =>
    authenticate("/api/v1/auth/signup", { loginId, password, nickname }),

  login: (loginId: string, password: string) =>
    authenticate("/api/v1/auth/login", { loginId, password }),

  coins: () => http<ApiCoin[]>("/api/v1/coins"),

  marketPrices: (currency = "KRW") =>
    http<MarketPrice[]>(`/api/v1/market/prices?currency=${encodeURIComponent(currency)}`),

  marketPrice: (symbol: string, currency = "KRW") =>
    http<MarketPrice>(
      `/api/v1/market/prices/${encodeURIComponent(symbol)}?currency=${encodeURIComponent(currency)}`,
    ),

  posts: (symbol: string) => http<ApiPost[]>(`/api/v1/communities/${symbol}/posts`),

  assets: () => http<AssetPortfolio>("/api/v1/me/assets"),

  createPost: (coinSymbol: string, title: string, content: string) =>
    http<ApiPost>("/api/v1/posts", {
      method: "POST",
      body: JSON.stringify({ coinSymbol, title, content }),
    }),

  like: (postId: number, liked: boolean) =>
    http<ApiPost>(`/api/v1/posts/${postId}/likes`, { method: liked ? "DELETE" : "POST" }),

  logout: async () => {
    try {
      await http<void>("/api/v1/auth/logout", { method: "POST" });
    } finally {
      clearAccessToken();
    }
  },
};
