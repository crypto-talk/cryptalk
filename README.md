# CRYPTALK

암호화폐 지갑으로 로그인하고, 코인 보유 여부와 자산 규모를 인증해 대화하는 코인별 커뮤니티입니다.

## 프로젝트 구조

```text
cryptalk/
├── frontend/   # Vinext/React 웹 애플리케이션
├── backend/    # Java 24 + Spring Boot API
└── docker-compose.yml
```

## 로컬 실행

Docker와 Node.js 22.13 이상이 필요합니다.

```bash
docker compose up -d mysql

cd backend
./gradlew bootRun

cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:3000`, API와 Swagger UI는 각각
`http://localhost:8080`, `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

## 구현 범위

- EVM 지갑 메시지 서명 로그인과 JWT/refresh cookie
- 코인별 커뮤니티, 게시글, 좋아요, 댓글 API
- Ethereum JSON-RPC 기반 ETH 잔액 인증
- 자산 공개 범위(정확한 금액/구간/비공개)와 게시 시점 자산 스냅샷
- MySQL 8.4 및 Liquibase 스키마 관리

실제 자산 인증을 사용하려면 백엔드에 `ETHEREUM_RPC_URL`과 `ETH_KRW_PRICE`를
설정해야 합니다. 설정하지 않은 경우 로그인과 커뮤니티 기능은 동작하지만 ETH 자산은
`UNAVAILABLE` 상태로 표시됩니다.
