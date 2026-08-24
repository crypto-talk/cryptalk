# CRYPTALK

이메일로 가입해 바로 참여하고, 선택적으로 암호화폐 지갑을 연결해 코인 보유 여부와 자산 규모를 인증하는 코인별 커뮤니티입니다. 기존 지갑 로그인도 계속 지원합니다.

## 프로젝트 구조

```text
cryptalk/
├── frontend/   # Vinext/React 웹 애플리케이션
├── backend/    # Java 24 + Spring Boot API
└── docker-compose.yml
```

## Docker Compose 실행

Docker Engine과 Docker Compose만 있으면 프론트엔드, 백엔드, MySQL, Nginx
게이트웨이를 함께 빌드하고 실행할 수 있습니다.

```bash
cp .env.example .env
# .env의 비밀번호, JWT secret, public origin을 수정
docker compose up -d --build
docker compose ps
```

- 서비스: `http://localhost` 또는 `.env`의 `HTTP_PORT`
- API: `http://localhost/api/v1`
- Swagger: `http://localhost/swagger-ui.html`
- MySQL과 애플리케이션 컨테이너는 외부에 직접 노출되지 않습니다.

운영 HTTPS는 이 Compose 앞단의 reverse proxy 또는 load balancer에서 종료하고,
`.env`의 `PUBLIC_ORIGIN=https://your-domain.example` 및
`AUTH_COOKIE_SECURE=true`를 설정하세요.

## GitHub에서 내려받아 재배포

배포 서버에서 GitHub 인증을 한 번 설정한 후 `deploy.sh`를 실행하면 private
저장소를 최초 clone하거나 최신 `main`을 fast-forward pull하고, 이미지를 다시
빌드한 다음 컨테이너를 교체합니다. GitHub 토큰은 `.env`나 Compose 파일에 넣지
않습니다.

```bash
chmod +x deploy.sh
CRYPTALK_DEPLOY_DIRECTORY="$HOME/cryptalk-deploy" ./deploy.sh
```

최초 실행은 `$CRYPTALK_DEPLOY_DIRECTORY/.env`를 생성하고 중단합니다. 해당 파일의
placeholder secret을 교체한 뒤 같은 명령을 다시 실행하세요.

다른 브랜치나 저장소를 배포할 때는 다음 변수를 사용할 수 있습니다.

```bash
CRYPTALK_BRANCH=main \
CRYPTALK_REPOSITORY_URL=https://github.com/ghwns9652/cryptalk.git \
CRYPTALK_DEPLOY_DIRECTORY="$HOME/cryptalk-deploy" \
./deploy.sh
```

수동 업데이트는 checkout 안에서 아래처럼 실행할 수도 있습니다.

```bash
git pull --ff-only
docker compose build --pull backend frontend
docker compose up -d --remove-orphans
```

## 개발 환경 실행

컨테이너 없이 개발할 때는 Docker와 Node.js 22.13 이상, Java 24가 필요합니다.

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

- 이메일 회원가입·로그인 및 EVM 지갑 메시지 서명 로그인
- 가입 계정에 EVM 지갑을 선택적으로 연결하는 소유권 인증
- BCrypt 비밀번호 해시와 JWT/refresh cookie
- 코인별 커뮤니티, 게시글, 좋아요, 댓글 API
- Ethereum JSON-RPC 기반 ETH 잔액 인증
- 자산 공개 범위(정확한 금액/구간/비공개)와 게시 시점 자산 스냅샷
- MySQL 8.4 및 Liquibase 스키마 관리

실제 자산 인증을 사용하려면 백엔드에 `ETHEREUM_RPC_URL`과 `ETH_KRW_PRICE`를
설정해야 합니다. 설정하지 않은 경우 로그인과 커뮤니티 기능은 동작하지만 ETH 자산은
`UNAVAILABLE` 상태로 표시됩니다.
