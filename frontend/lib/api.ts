const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export type ApiCoin = { id: number; symbol: string; name: string; chainType: string; accentColor: string };
export type Member = { id: number; nickname: string; avatarColor: string; walletAddress: string | null; assetVisibility: string };
export type Asset = { symbol: string; quantity: number; valueKrw: number; verified: boolean; status: string; capturedAt: string };
export type ApiPost = {
  id: number; coinSymbol: string; title: string; content: string;
  author: { id: number; nickname: string; avatarColor: string; walletAddress: string | null };
  verifiedHolder: boolean; assetValueKrw?: number; assetDisplay: string;
  likes: number; comments: number; liked: boolean; createdAt: string;
};

let accessToken: string | null = typeof window === "undefined" ? null : sessionStorage.getItem("cryptalk_access");

async function request<T>(path: string, init: RequestInit = {}, retry = true): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.body) headers.set("Content-Type", "application/json");
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  const response = await fetch(`${API_URL}${path}`, { ...init, headers, credentials: "include" });
  if (response.status === 401 && retry && !path.startsWith("/api/v1/auth/")) {
    const refreshed = await refreshSession();
    if (refreshed) return request<T>(path, init, false);
  }
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: "요청을 처리하지 못했습니다." }));
    throw new Error(body.message ?? "요청을 처리하지 못했습니다.");
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

function setToken(token: string | null) {
  accessToken = token;
  if (token) sessionStorage.setItem("cryptalk_access", token);
  else sessionStorage.removeItem("cryptalk_access");
}

export async function refreshSession(): Promise<Member | null> {
  try {
    const result = await request<{ accessToken: string; member: Member }>("/api/v1/auth/refresh", { method: "POST" }, false);
    setToken(result.accessToken); return result.member;
  } catch { setToken(null); return null; }
}

export async function connectInjectedWallet(): Promise<Member> {
  const { ethereum, walletAddress } = await injectedWallet();
  const nonce = await request<{ nonceId: string; message: string }>("/api/v1/auth/nonce", { method: "POST", body: JSON.stringify({ walletAddress }) });
  const signature = await ethereum.request({ method: "personal_sign", params: [nonce.message, walletAddress] }) as string;
  const auth = await request<{ accessToken: string; member: Member }>("/api/v1/auth/wallet", {
    method: "POST", body: JSON.stringify({ walletAddress, nonceId: nonce.nonceId, signature }),
  });
  setToken(auth.accessToken); return auth.member;
}

async function injectedWallet() {
  const ethereum = (window as typeof window & { ethereum?: { request(args: { method: string; params?: unknown[] }): Promise<unknown> } }).ethereum;
  if (!ethereum) throw new Error("EVM 지갑 확장 프로그램을 설치해 주세요.");
  const accounts = await ethereum.request({ method: "eth_requestAccounts" }) as string[];
  const walletAddress = accounts[0];
  if (!walletAddress) throw new Error("지갑 계정을 선택해 주세요.");
  return { ethereum, walletAddress };
}

export async function linkInjectedWallet(): Promise<Member> {
  const { ethereum, walletAddress } = await injectedWallet();
  const nonce = await request<{ nonceId: string; message: string }>("/api/v1/me/wallet/nonce", { method: "POST", body: JSON.stringify({ walletAddress }) });
  const signature = await ethereum.request({ method: "personal_sign", params: [nonce.message, walletAddress] }) as string;
  return request<Member>("/api/v1/me/wallet", {
    method: "POST", body: JSON.stringify({ walletAddress, nonceId: nonce.nonceId, signature }),
  });
}

export const api = {
  signup: async (email: string, password: string, nickname: string) => {
    const auth = await request<{ accessToken: string; member: Member }>("/api/v1/auth/signup", { method: "POST", body: JSON.stringify({ email, password, nickname }) });
    setToken(auth.accessToken); return auth.member;
  },
  emailLogin: async (email: string, password: string) => {
    const auth = await request<{ accessToken: string; member: Member }>("/api/v1/auth/login", { method: "POST", body: JSON.stringify({ email, password }) });
    setToken(auth.accessToken); return auth.member;
  },
  coins: () => request<ApiCoin[]>("/api/v1/coins"),
  posts: (symbol: string) => request<ApiPost[]>(`/api/v1/communities/${symbol}/posts`),
  assets: () => request<Asset[]>("/api/v1/me/assets"),
  createPost: (coinSymbol: string, title: string, content: string) => request<ApiPost>("/api/v1/posts", { method: "POST", body: JSON.stringify({ coinSymbol, title, content }) }),
  like: (postId: number, liked: boolean) => request<ApiPost>(`/api/v1/posts/${postId}/likes`, { method: liked ? "DELETE" : "POST" }),
  logout: async () => { await request<void>("/api/v1/auth/logout", { method: "POST" }, false); setToken(null); },
};
