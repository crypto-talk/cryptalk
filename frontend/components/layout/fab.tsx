/**
 * 좁은 화면(<900px)에서만 보이는 글쓰기 버튼.
 * 넓은 화면에서는 헤더의 글쓰기가 같은 자리를 맡는다.
 *
 * 글쓰기 화면은 3단계다. 그때까지는 헤더와 같은 이유로 비활성이다.
 */
export default function Fab() {
  return (
    <div className="hd-fab">
      <button
        type="button"
        className="hd-btn hd-btn-primary"
        disabled
        title="글쓰기 화면은 아직 준비 중입니다"
      >
        글쓰기
      </button>
    </div>
  );
}
