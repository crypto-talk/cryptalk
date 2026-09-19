package com.cryptalk.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "모든 API 오류의 공통 응답")
public record ErrorResponse(
    @Schema(description = "클라이언트 분기용 고정 코드", example = "INVALID_CREDENTIALS") ErrorCode code,
    @Schema(description = "사람이 읽을 수 있는 참고 메시지", example = "아이디 또는 비밀번호가 올바르지 않습니다.") String message,
    @Schema(description = "오류 발생 시각") Instant timestamp
) {
    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code, code.message(), Instant.now());
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code, message, Instant.now());
    }
}
