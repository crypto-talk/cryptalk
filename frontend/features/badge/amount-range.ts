/**
 * 보유 수량 구간 표기.
 *
 * ⚠️ 지갑 특정 방지 규칙이다. 정확한 수량을 그대로 보여주면 온체인에서
 * 잔액을 역으로 맞춰 지갑 주인을 찾아낼 수 있다. 그래서 글에 붙는 수량은
 * 항상 넓은 구간으로만 나간다. 이 파일 밖에서 수량을 표기하지 않는다
 * (구조 규칙 3).
 *
 * 구간은 10배씩 벌린다. 좁히면 특정 가능성이 올라가므로,
 * 바꿀 때는 그 점을 먼저 따져야 한다.
 */

export type AmountRange = {
  /** 구간 하한(이상). */
  min: number;
  /** 구간 상한(미만). 마지막 구간은 null. */
  max: number | null;
  label: string;
};

const RANGES: readonly AmountRange[] = [
  { min: 0, max: 1, label: "1 미만" },
  { min: 1, max: 10, label: "1~10" },
  { min: 10, max: 100, label: "10~100" },
  { min: 100, max: 1_000, label: "100~1,000" },
  { min: 1_000, max: 10_000, label: "1,000~10,000" },
  { min: 10_000, max: null, label: "10,000 이상" },
];

/** 수량이 속한 구간. 음수·NaN 은 구간 없음으로 본다. */
export function amountRangeOf(quantity: number): AmountRange | null {
  if (!Number.isFinite(quantity) || quantity < 0) return null;
  return (
    RANGES.find((range) => quantity >= range.min && (range.max === null || quantity < range.max)) ??
    null
  );
}

/**
 * 글에 붙는 수량 표기. `10~100 ETH`
 *
 * 수량을 모르면 빈 문자열이 아니라 null 을 돌려준다. 호출하는 쪽이
 * 자리를 비울지 말지 정한다.
 */
export function amountRangeLabel(symbol: string, quantity: number): string | null {
  const range = amountRangeOf(quantity);
  if (!range) return null;
  return `${range.label} ${symbol.toUpperCase()}`;
}
