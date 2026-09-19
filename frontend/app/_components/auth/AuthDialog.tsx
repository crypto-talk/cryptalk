"use client";

import { FormEvent, useEffect, useRef, useState } from "react";
import { api, type Member } from "../../../lib/api";

export type AuthMode = "login" | "signup";

type Props = {
  mode: AuthMode;
  onModeChange: (mode: AuthMode) => void;
  onClose: () => void;
  onAuthenticated: (member: Member) => void;
};

/**
 * 이메일 로그인·회원가입 모달.
 *
 * 인증 자체는 lib/api.ts에 이미 구현돼 있습니다. 여기서는 화면만 붙입니다.
 * 토큰은 sessionStorage, 갱신 토큰은 쿠키라 credentials: "include"가 필요하고,
 * 그래서 배포본에서는 백엔드의 PUBLIC_ORIGIN에 배포 도메인이 들어 있어야 합니다.
 */
export default function AuthDialog({ mode, onModeChange, onClose, onAuthenticated }: Props) {
  const [error, setError] = useState("");
  const [pending, setPending] = useState(false);
  const firstFieldRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    firstFieldRef.current?.focus();
  }, [mode]);

  useEffect(() => {
    const onKey = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    setPending(true);
    const form = new FormData(event.currentTarget);
    const loginId = String(form.get("loginId") ?? "");
    const password = String(form.get("password") ?? "");

    try {
      const member =
        mode === "signup"
          ? await api.signup(loginId, password, String(form.get("nickname") ?? ""))
          : await api.login(loginId, password);
      onAuthenticated(member);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "인증에 실패했습니다.");
    } finally {
      setPending(false);
    }
  };

  return (
    <div className="hd-backdrop" onMouseDown={onClose}>
      <section
        className="hd-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="hd-auth-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <h2 id="hd-auth-title" className="hd-t-h2">
          {mode === "signup" ? "시작하기" : "로그인"}
        </h2>
        <p className="hd-t-sm hd-muted" style={{ marginTop: 8 }}>
          연결 안 해도 읽고 쓸 수 있습니다. 지갑은 나중에 붙여도 됩니다.
        </p>

        <form className="hd-form" onSubmit={submit}>
          {mode === "signup" ? (
            <input
              ref={firstFieldRef}
              className="hd-field"
              name="nickname"
              aria-label="닉네임"
              placeholder="닉네임"
              minLength={2}
              maxLength={40}
              required
            />
          ) : null}
          {/* 백엔드 계약이 email → loginId 로 바뀌었습니다. type도 text입니다. */}
          <input
            ref={mode === "login" ? firstFieldRef : undefined}
            className="hd-field"
            name="loginId"
            type="text"
            aria-label="아이디"
            placeholder="아이디"
            autoComplete="username"
            required
          />
          <input
            className="hd-field"
            name="password"
            type="password"
            aria-label="비밀번호"
            placeholder="비밀번호 (8자 이상)"
            minLength={8}
            maxLength={72}
            autoComplete={mode === "signup" ? "new-password" : "current-password"}
            required
          />
          <button
            type="submit"
            className="hd-btn hd-btn-primary hd-btn-block"
            style={{ marginTop: 8 }}
            disabled={pending}
          >
            {pending ? "확인 중…" : mode === "signup" ? "계정 만들기" : "로그인"}
          </button>
        </form>

        {error ? (
          <p role="alert" className="hd-error">
            {error}
          </p>
        ) : null}

        <div style={{ marginTop: 16, display: "flex", justifyContent: "space-between" }}>
          <button
            type="button"
            className="hd-link-button"
            onClick={() => onModeChange(mode === "login" ? "signup" : "login")}
          >
            {mode === "login" ? "계정 만들기" : "이미 계정이 있어요"}
          </button>
          <button type="button" className="hd-link-button hd-muted" onClick={onClose}>
            닫기
          </button>
        </div>
      </section>
    </div>
  );
}
