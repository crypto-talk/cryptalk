import { config } from "@/lib/config";

/**
 * 백엔드 요청 래퍼 (C-4).
 *
 * 범위가 의도적으로 좁다. 지금 하는 일은 셋뿐이다.
 *   1. baseURL 을 붙인다
 *   2. `credentials: "include"` 로 쿠키를 싣는다
 *   3. 실패 응답을 `{ code, message }` 로 정규화한다
 *
 * 하지 않는 것과 그 이유:
 *   - 액세스 토큰 부착: 지금은 sessionStorage 에 있고 httpOnly 쿠키로 옮기는
 *     백엔드 작업이 진행 중이다(B-3). 지금 구현하면 곧 다시 쓴다.
 *   - 401 재시도: 에러 응답 표준이 아직 백엔드와 합의되지 않았다(C-5).
 *   - 서버 컴포넌트에서 `cookies()` 를 읽어 헤더로 전달: 위 둘이 정해진 뒤에 붙는다.
 *
 * 그때까지 로그인이 필요한 요청은 기존 `lib/api.ts` 를 계속 쓴다.
 */

export type ApiErrorShape = {
  /** 백엔드와 합의 예정. 합의 전까지는 HTTP 상태 문자열이 들어간다. */
  code: string;
  message: string;
};

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;

  constructor(status: number, shape: ApiErrorShape) {
    super(shape.message);
    this.name = "ApiError";
    this.status = status;
    this.code = shape.code;
  }
}

const DEFAULT_MESSAGE = "요청을 처리하지 못했습니다.";

export async function http<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${config.apiUrl}${path}`, {
    ...init,
    headers,
    credentials: "include",
  });

  if (!response.ok) {
    throw new ApiError(response.status, await normalizeError(response));
  }

  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

async function normalizeError(response: Response): Promise<ApiErrorShape> {
  const body: unknown = await response.json().catch(() => null);
  if (body && typeof body === "object") {
    const record = body as Record<string, unknown>;
    return {
      code: typeof record.code === "string" ? record.code : `HTTP_${response.status}`,
      message: typeof record.message === "string" ? record.message : DEFAULT_MESSAGE,
    };
  }
  return { code: `HTTP_${response.status}`, message: DEFAULT_MESSAGE };
}
