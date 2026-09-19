import { describe, expect, it } from "vitest";
import { formatRelativeTime } from "@/lib/format/time";

const NOW = new Date("2026-09-19T12:00:00Z");

describe("formatRelativeTime", () => {
  it("1분 미만은 방금", () => {
    expect(formatRelativeTime("2026-09-19T11:59:30Z", NOW)).toBe("방금");
  });

  it("분·시간·일 단위로 떨어진다", () => {
    expect(formatRelativeTime("2026-09-19T11:48:00Z", NOW)).toBe("12분 전");
    expect(formatRelativeTime("2026-09-19T09:00:00Z", NOW)).toBe("3시간 전");
    expect(formatRelativeTime("2026-09-17T12:00:00Z", NOW)).toBe("2일 전");
  });

  it("일주일이 넘으면 날짜로 떨어진다", () => {
    expect(formatRelativeTime("2026-09-01T12:00:00Z", NOW)).toContain("9월 1일");
    expect(formatRelativeTime("2025-09-01T12:00:00Z", NOW)).toContain("2025");
  });

  it("미래 시각은 방금으로 막는다", () => {
    expect(formatRelativeTime("2026-09-19T12:05:00Z", NOW)).toBe("방금");
  });

  it("없거나 깨진 값은 빈 문자열", () => {
    expect(formatRelativeTime(null, NOW)).toBe("");
    expect(formatRelativeTime(undefined, NOW)).toBe("");
    expect(formatRelativeTime("", NOW)).toBe("");
    expect(formatRelativeTime("어제", NOW)).toBe("");
  });
});
