"use client";

import { useState } from "react";
import { QueryClientProvider } from "@tanstack/react-query";
import { createQueryClient } from "@/lib/query";

/**
 * 클라이언트 프로바이더 모음.
 *
 * 루트 레이아웃은 서버 컴포넌트로 남겨야 하므로(구조 규칙 6) `"use client"`
 * 는 이 파일에만 붙인다. 레이아웃은 이걸 렌더하기만 한다.
 *
 * QueryClient 를 모듈 최상단이 아니라 `useState` 안에서 만드는 이유: 서버에서
 * 모듈이 공유되면 요청끼리 캐시가 섞인다.
 */
export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(createQueryClient);

  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
