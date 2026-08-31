# CrypTalk Backend

Java 24와 Spring Boot 3.5.16으로 구현한 CrypTalk API입니다.

## 로컬 실행

1. 저장소 루트에서 `docker compose up -d mysql`
2. `cd backend && ./gradlew bootRun`
3. Swagger UI: http://localhost:8080/swagger-ui.html

기본 설정은 로컬 개발용입니다. 운영 환경에서는 `DB_*`, `JWT_SECRET`,
`ETHEREUM_RPC_URL`, `ETH_KRW_PRICE`, `CORS_ALLOWED_ORIGINS`를 반드시 설정하세요.

로그인한 계정의 지갑 연결은 EVM 지갑의 `personal_sign` 서명을 검증합니다. ETH 보유량 인증은
설정된 Ethereum JSON-RPC를 통해 서버에서 조회하며, RPC 주소가 없으면 인증 상태를
`UNAVAILABLE`로 반환합니다.
