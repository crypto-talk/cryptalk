/**
 * 헤더 + 사이드바(≥900px) / 하단 탭(<900px) 화면군 (A-3).
 *
 * 랜딩·방 게시판·글 상세·공개 프로필이 2단계에서 여기로 들어온다.
 * 지금은 경계만 세워 둔 상태라 children 을 그대로 흘린다.
 */
export default function ShellLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>;
}
