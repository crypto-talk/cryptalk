import { describe, expect, it } from "vitest";
import { UNKNOWN_HOLDING_LABEL, holdingMonthsSince, holdingPeriodLabel } from "./holding-period";

describe("holdingPeriodLabel", () => {
  it("값이 없으면 고정 문구를 쓴다", () => {
    expect(holdingPeriodLabel(null)).toBe(UNKNOWN_HOLDING_LABEL);
    expect(holdingPeriodLabel(undefined)).toBe(UNKNOWN_HOLDING_LABEL);
  });

  it("1개월 미만은 한 덩어리로 묶는다", () => {
    expect(holdingPeriodLabel(0)).toBe("1개월 미만 보유");
    expect(holdingPeriodLabel(0.9)).toBe("1개월 미만 보유");
  });

  it("연·월 경계", () => {
    expect(holdingPeriodLabel(1)).toBe("1개월 보유");
    expect(holdingPeriodLabel(11)).toBe("11개월 보유");
    expect(holdingPeriodLabel(12)).toBe("1년 보유");
    expect(holdingPeriodLabel(13)).toBe("1년 1개월 보유");
    expect(holdingPeriodLabel(24)).toBe("2년 보유");
    expect(holdingPeriodLabel(27)).toBe("2년 3개월 보유");
  });

  it("월 아래 단위는 표기에 남지 않는다", () => {
    expect(holdingPeriodLabel(8.7)).toBe("8개월 보유");
  });

  it("음수와 NaN 은 미확인으로 떨어진다", () => {
    expect(holdingPeriodLabel(-1)).toBe(UNKNOWN_HOLDING_LABEL);
    expect(holdingPeriodLabel(Number.NaN)).toBe(UNKNOWN_HOLDING_LABEL);
  });
});

describe("holdingMonthsSince", () => {
  const at = (iso: string) => new Date(iso);

  it("날짜를 못 넘겼으면 그 달을 세지 않는다", () => {
    expect(holdingMonthsSince(at("2026-01-15T00:00:00Z"), at("2026-02-14T00:00:00Z"))).toBe(0);
    expect(holdingMonthsSince(at("2026-01-15T00:00:00Z"), at("2026-02-15T00:00:00Z"))).toBe(1);
  });

  it("해를 넘겨도 개월로 센다", () => {
    expect(holdingMonthsSince(at("2024-11-01T00:00:00Z"), at("2026-02-01T00:00:00Z"))).toBe(15);
  });

  it("올림하지 않는다. 29일이 지나도 0개월이다", () => {
    expect(holdingMonthsSince(at("2026-01-01T00:00:00Z"), at("2026-01-30T00:00:00Z"))).toBe(0);
  });

  it("미래 시각은 null", () => {
    expect(holdingMonthsSince(at("2026-05-01T00:00:00Z"), at("2026-04-01T00:00:00Z"))).toBeNull();
  });
});
