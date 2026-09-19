import type { Member } from "../../../lib/api";
import Logo from "./Logo";

type Props = {
  member: Member | null;
  onLogin: () => void;
  onSignup: () => void;
  onLogout: () => void;
  onWrite: () => void;
};

export default function Header({ member, onLogin, onSignup, onLogout, onWrite }: Props) {
  return (
    <header className="hd-header">
      <a href="/" className="hd-brand" aria-label="Hodlit 홈">
        <Logo size={32} background="#6E56F0" foreground="#FFFFFF" />
        <span className="hd-brand-name">Hodlit</span>
      </a>

      {/* 통합 검색은 백엔드에 /search가 없어 아직 비활성입니다. */}
      <button type="button" className="hd-search" disabled title="검색은 준비 중입니다">
        <span className="hd-search-icon" />
        <span className="hd-ellipsis">코인 · 지갑 · 글 검색</span>
      </button>

      <div className="hd-header-actions">
        {member ? (
          <>
            <span className="hd-nick hd-ellipsis">{member.nickname}</span>
            <button
              type="button"
              className="hd-btn hd-btn-primary hd-only-desktop"
              onClick={onWrite}
            >
              글쓰기
            </button>
            <button type="button" className="hd-btn" onClick={onLogout}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            <button type="button" className="hd-btn" onClick={onLogin}>
              로그인
            </button>
            <button
              type="button"
              className="hd-btn hd-btn-primary hd-only-desktop"
              onClick={onWrite}
            >
              글쓰기
            </button>
            <button type="button" className="hd-btn" onClick={onSignup}>
              시작하기
            </button>
          </>
        )}
      </div>
    </header>
  );
}
