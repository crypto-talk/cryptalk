/**
 * 보유 기간 표기.
 *
 * ⚠️ 지갑 특정 방지 규칙이다. 매수·매도 시각을 그대로 드러내면 온체인
 * 트랜잭션과 맞춰볼 수 있다. 그래서 기간은 월 단위로만 나가고, 일·시각은
 * 어떤 경로로도 표기하지 않는다.
 *
 * 백엔드의 `holdingMonths` 는 EVM 인덱서가 붙기 전까지 항상 null 이다.
 * 그동안은 고정 문구를 쓴다 (G-6). 자리를 비우면 인덱서가 붙을 때
 * 레이아웃이 흔들린다.
 */

export const UNKNOWN_HOLDING_LABEL = "보유 기간 미확인";

/**
 * 개월 수를 사람이 읽는 기간으로. 1개월 미만은 한 덩어리로 뭉갠다.
 *
 * - null    → `보유 기간 미확인`
 * - 0       → `1개월 미만 보유`
 * - 8       → `8개월 보유`
 * - 12      → `1년 보유`
 * - 27      → `2년 3개월 보유`
 */
export function holdingPeriodLabel(holdingMonths: number | null | undefined): string {
  if (holdingMonths === null || holdingMonths === undefined) {
    return UNKNOWN_HOLDING_LABEL;
  }
  if (!Number.isFinite(holdingMonths) || holdingMonths < 0) {
    return UNKNOWN_HOLDING_LABEL;
  }

  const months = Math.floor(holdingMonths);
  if (months < 1) return "1개월 미만 보유";
  if (months < 12) return `${months}개월 보유`;

  const years = Math.floor(months / 12);
  const rest = months % 12;
  return rest === 0 ? `${years}년 보유` : `${years}년 ${rest}개월 보유`;
}

/**
 * 보유 시작 시각을 개월 수로 내린다.
 *
 * 내림(`floor`)이다. 반올림하면 실제보다 긴 기간이 표시되어, 매수 시점을
 * 한 달 앞으로 추정할 여지를 준다. 짧게 말하는 쪽이 안전하다.
 */
export function holdingMonthsSince(holdingSince: Date, now: Date): number | null {
  const since = holdingSince.getTime();
  const current = now.getTime();
  if (!Number.isFinite(since) || !Number.isFinite(current)) return null;
  if (current < since) return null;

  let months =
    (now.getUTCFullYear() - holdingSince.getUTCFullYear()) * 12 +
    (now.getUTCMonth() - holdingSince.getUTCMonth());

  // 같은 달의 날짜를 아직 못 넘겼으면 한 달을 뺀다.
  if (now.getUTCDate() < holdingSince.getUTCDate()) months -= 1;

  return Math.max(0, months);
}
