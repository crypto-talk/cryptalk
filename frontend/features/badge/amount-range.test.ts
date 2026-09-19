import { describe, expect, it } from "vitest";
import { amountRangeLabel, amountRangeOf } from "./amount-range";

describe("amountRangeOf", () => {
  it("구간 경계는 하한 이상, 상한 미만이다", () => {
    expect(amountRangeOf(0.999)?.label).toBe("1 미만");
    expect(amountRangeOf(1)?.label).toBe("1~10");
    expect(amountRangeOf(9.999)?.label).toBe("1~10");
    expect(amountRangeOf(10)?.label).toBe("10~100");
    expect(amountRangeOf(999.99)?.label).toBe("100~1,000");
    expect(amountRangeOf(1_000)?.label).toBe("1,000~10,000");
  });

  it("마지막 구간은 상한이 없다", () => {
    expect(amountRangeOf(10_000)?.label).toBe("10,000 이상");
    expect(amountRangeOf(9_999_999)?.label).toBe("10,000 이상");
  });

  it("0 은 첫 구간에 들어간다", () => {
    expect(amountRangeOf(0)?.label).toBe("1 미만");
  });

  it("음수와 NaN 은 구간이 없다", () => {
    expect(amountRangeOf(-1)).toBeNull();
    expect(amountRangeOf(Number.NaN)).toBeNull();
    expect(amountRangeOf(Number.POSITIVE_INFINITY)).toBeNull();
  });
});

describe("amountRangeLabel", () => {
  it("심볼을 대문자로 붙인다", () => {
    expect(amountRangeLabel("eth", 42)).toBe("10~100 ETH");
  });

  it("정확한 수량이 문자열에 남지 않는다", () => {
    const label = amountRangeLabel("ETH", 42.195);
    expect(label).not.toContain("42.195");
    expect(label).toBe("10~100 ETH");
  });

  it("구간이 없으면 null", () => {
    expect(amountRangeLabel("ETH", -3)).toBeNull();
  });
});
