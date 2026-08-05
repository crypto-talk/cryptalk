# CRYPTALK

암호화폐 지갑으로 로그인하고, 코인 보유 여부와 자산 규모를 인증해 대화하는 코인별 커뮤니티입니다.

## 프로젝트 구조

```text
cryptalk/
├── frontend/   # 현재 구현된 반응형 웹 애플리케이션
└── backend/    # 향후 API와 블록체인 연동을 구현할 서버 영역
```

## 프론트엔드 실행

Node.js 22.13 이상이 필요합니다.

```bash
cd frontend
npm install
npm run dev
```

프로덕션 빌드는 다음 명령으로 확인합니다.

```bash
cd frontend
npm run build
```

현재 지갑 연결, 보유 자산과 게시글 데이터는 프론트엔드 데모입니다. 실제 지갑 서명, 온체인 자산 조회, 사용자 및 게시글 저장 기능은 백엔드 연동 단계에서 구현할 예정입니다.
