package com.cryptalk.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "클라이언트가 분기에 사용하는 고정 오류 코드", enumAsRef = true)
public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청값을 확인해 주세요."),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식을 확인해 주세요."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증 토큰이 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    LOGIN_ID_TAKEN(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    NICKNAME_TAKEN(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    COIN_NOT_FOUND(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "지갑을 찾을 수 없습니다."),
    WALLET_ALREADY_LINKED(HttpStatus.CONFLICT, "다른 계정에 연결된 지갑입니다."),
    WALLET_CHALLENGE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "지갑 인증 요청을 찾을 수 없습니다."),
    WALLET_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "지갑 서명이 올바르지 않거나 만료되었습니다."),
    ASSET_BALANCE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EVM 지갑 잔액을 모두 확인하지 못했습니다."),
    MARKET_PRICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "실시간 자산 가격을 조회하지 못했습니다. 잠시 후 다시 시도해 주세요."),
    MARKET_PRICE_NOT_SUPPORTED(HttpStatus.UNPROCESSABLE_ENTITY, "이 자산은 실시간 시세 조회를 지원하지 않습니다."),
    INVALID_CURRENCY(HttpStatus.BAD_REQUEST, "가격 통화 형식을 확인해 주세요."),
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신은 팔로우할 수 없습니다."),
    RESOURCE_FORBIDDEN(HttpStatus.FORBIDDEN, "요청한 리소스를 변경할 권한이 없습니다."),
    EMPTY_MEDIA_FILE(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다."),
    MEDIA_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "파일은 25MB 이하여야 합니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 이미지 또는 영상 형식입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 파일 이름입니다."),
    MEDIA_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장하지 못했습니다."),
    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."),
    MEDIA_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 업로드한 미디어만 사용할 수 있습니다."),
    MEDIA_IN_USE(HttpStatus.CONFLICT, "게시글에 연결된 미디어는 직접 제거할 수 없습니다."),
    MEDIA_NOT_UPLOADED(HttpStatus.BAD_REQUEST, "업로드 기록이 없는 미디어 URL입니다."),
    MEDIA_ALREADY_LINKED(HttpStatus.CONFLICT, "이미 다른 게시글에 연결된 미디어입니다."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "올바르지 않은 피드 cursor입니다."),
    MEDIA_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "미디어 형식이 필요합니다."),
    DUPLICATE_MEDIA(HttpStatus.BAD_REQUEST, "같은 미디어를 중복 등록할 수 없습니다."),
    INVALID_MEDIA_URL(HttpStatus.BAD_REQUEST, "미디어 URL은 업로드 URL 또는 HTTPS URL이어야 합니다."),
    INVALID_YOUTUBE_URL(HttpStatus.BAD_REQUEST, "올바르지 않은 YouTube 또는 Shorts URL이 아닙니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() { return status; }
    public String message() { return message; }
}
