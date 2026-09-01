const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

// ─── Types ────────────────────────────────────────────────────────────────────

export type Member = {
  id: number;
  nickname: string;
  avatarColor: string;
  walletAddress: string | null;
  assetVisibility: string;
};

export type AuthResponse = {
  accessToken: string;
  tokenType: string;
  member: Member;
};

export type SignupRequest = {
  email: string;
  password: string;
  nickname: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

// ─── Token Store ──────────────────────────────────────────────────────────────

const TOKEN_KEY = "cryptalk_access";

function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return sessionStorage.getItem(TOKEN_KEY);
}

function saveToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token);
}

function clearToken(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}

// ─── Core Fetch ───────────────────────────────────────────────────────────────

async function authFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);

  if (init.body) {
    headers.set("Content-Type", "application/json");
  }

  const token = getToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers,
    credentials: "include",
  });

  if (!response.ok) {
    const body = await response
      .json()
      .catch(() => ({ message: "요청을 처리하지 못했습니다." }));
    throw new Error(body.message ?? "요청을 처리하지 못했습니다.");
  }

  if (response.status === 204) return undefined as T;

  return response.json() as Promise<T>;
}

// ─── Auth API ─────────────────────────────────────────────────────────────────

export const AuthApi = {
  /**
   * 이메일/비밀번호로 로그인합니다.
   * 성공 시 accessToken을 sessionStorage에 저장하고 AuthResponse를 반환합니다.
   */
  async login(body: LoginRequest): Promise<AuthResponse> {
    const result = await authFetch<AuthResponse>("/api/v1/auth/login", {
      method: "POST",
      body: JSON.stringify(body),
    });
    saveToken(result.accessToken);
    return result;
  },

  /**
   * 이메일/비밀번호/닉네임으로 회원가입합니다.
   * 성공 시 accessToken을 sessionStorage에 저장하고 AuthResponse를 반환합니다.
   */
  async signup(body: SignupRequest): Promise<AuthResponse> {
    const result = await authFetch<AuthResponse>("/api/v1/auth/signup", {
      method: "POST",
      body: JSON.stringify(body),
    });
    saveToken(result.accessToken);
    return result;
  },

  /**
   * 쿠키의 refresh token으로 새 accessToken을 발급받습니다.
   * 세션 복원 실패 시 null을 반환합니다.
   */
  async refresh(): Promise<AuthResponse | null> {
    try {
      const result = await authFetch<AuthResponse>("/api/v1/auth/refresh", {
        method: "POST",
      });
      saveToken(result.accessToken);
      return result;
    } catch {
      clearToken();
      return null;
    }
  },

  /**
   * 로그아웃합니다. 서버 세션과 로컬 토큰을 모두 제거합니다.
   */
  async logout(): Promise<void> {
    await authFetch<void>("/api/v1/auth/logout", { method: "POST" }).catch(
      () => undefined,
    );
    clearToken();
  },
};
