# SNS API 명세

기본 경로는 `/api/v1`입니다. Swagger UI는 배포 서버의 `/swagger-ui.html`, OpenAPI JSON은
`/v3/api-docs`에서 확인할 수 있습니다. 변경 API는 `Authorization: Bearer {accessToken}`이
필요하고, 피드·게시글·댓글·팔로우 목록 조회는 로그인 없이도 사용할 수 있습니다.

## 엔드포인트

| 기능 | Method | Path | 인증 |
|---|---|---|---|
| 랜딩 피드 | GET | `/feed?size=30&cursor=...` | 선택 |
| 팔로잉 피드 | GET | `/feed/following?size=30&cursor=...` | 필요 |
| 코인별 글 | GET | `/communities/{symbol}/posts?size=30` | 선택 |
| 글 상세 | GET | `/posts/{postId}` | 선택 |
| 글 작성/수정/삭제 | POST / PUT / DELETE | `/posts`, `/posts/{postId}` | 필요, 수정·삭제는 작성자만 |
| 미디어 업로드/조회/삭제 | POST / GET / DELETE | `/media`, `/media/{fileName}` | 조회 외 필요 |
| 댓글 목록/작성 | GET / POST | `/posts/{postId}/comments` | 작성만 필요 |
| 댓글 수정/삭제 | PATCH / DELETE | `/comments/{commentId}` | 필요, 작성자만 |
| 좋아요 | POST / DELETE | `/posts/{postId}/likes` | 필요 |
| 재게시 | POST / DELETE | `/posts/{postId}/reposts` | 필요 |
| 북마크 | POST / DELETE | `/posts/{postId}/bookmarks` | 필요 |
| 내 북마크 | GET | `/me/bookmarks` | 필요 |
| 팔로우/취소 | POST / DELETE | `/members/{memberId}/follow` | 필요 |
| 팔로우 통계 | GET | `/members/{memberId}/social` | 선택 |
| 팔로워/팔로잉 목록 | GET | `/members/{memberId}/followers`, `/following` | 불필요 |
| 현재 자산 가격 | GET | `/market/prices/{symbol}?currency=USD` | 불필요 |

조회 응답의 `liked`, `reposted`, `bookmarked`, `followedByMe`는 로그인 사용자를 기준으로
계산됩니다. 비로그인 조회에서는 `false`입니다. `size`는 1~100 범위로 보정됩니다.

## Cursor 피드

첫 요청에서는 `cursor`를 생략합니다. 다음 페이지는 응답의 `nextCursor`를 그대로 전달합니다.
cursor 내부 형식은 서버 구현 세부사항이므로 클라이언트가 생성하거나 해석하지 않습니다.

```json
{
  "items": [
    {
      "eventType": "REPOST",
      "occurredAt": "2026-09-01T10:05:00Z",
      "actor": { "id": 9, "nickname": "재게시자", "avatarColor": "#...", "walletAddress": null },
      "post": { "id": 42, "title": "ETH 분석" }
    },
    {
      "eventType": "POST",
      "occurredAt": "2026-09-01T10:00:00Z",
      "actor": { "id": 7, "nickname": "작성자", "avatarColor": "#...", "walletAddress": null },
      "post": { "id": 42, "title": "ETH 분석" }
    }
  ],
  "nextCursor": "MjAyNi0wOS0wMVQxMDowMDowMFp8UE9TVHw0Mnw3",
  "hasMore": true
}
```

- `eventType=POST`: 원글 작성 이벤트
- `eventType=REPOST`: 재게시 이벤트이며 `actor`는 재게시한 사용자
- `/feed/following`: 내가 팔로우한 사용자의 원글과 재게시 이벤트만 반환
- 마지막 페이지에서는 `hasMore=false`, `nextCursor`는 생략됨

## 사진·영상 업로드

`POST /media`에 `multipart/form-data`의 `file` 필드로 전송합니다. JPEG, PNG, WebP, GIF,
MP4, WebM, MOV를 지원하며 파일당 최대 크기는 25MB입니다.

```http
POST /api/v1/media
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data

file=@chart.png
```

```json
{
  "mediaType": "IMAGE",
  "url": "/api/v1/media/22bb2cdd-832f-4a90-854c-66d73a982873.png",
  "contentType": "image/png",
  "size": 42839
}
```

반환된 `url`을 글 작성 요청의 `media[].url`에 넣습니다. 한 글에는 최대 8개를 순서대로
넣을 수 있습니다. 운영 파일은 Docker named volume `cryptalk-media`에 보존됩니다.
업로드 파일은 업로드한 계정만 사용할 수 있습니다.

아직 게시글에 연결하지 않은 파일은 `DELETE /media/{fileName}`으로 삭제할 수 있습니다.
게시글에 연결된 파일은 직접 삭제할 수 없으며 글 수정에서 제외하거나 글을 삭제해야 합니다.
글 수정·삭제 transaction이 성공하면 서버가 연결이 끊긴 실제 파일도 함께 삭제합니다.

## 글 작성

```http
POST /api/v1/posts
Authorization: Bearer {accessToken}
Content-Type: application/json
```

```json
{
  "coinSymbol": "ETH",
  "title": "ETH 1시간봉 분석",
  "content": "지지 구간을 확인했습니다.",
  "media": [
    {
      "type": "IMAGE",
      "url": "/api/v1/media/22bb2cdd-832f-4a90-854c-66d73a982873.png",
      "thumbnailUrl": null
    }
  ],
  "tradingViewSymbol": "BINANCE:ETHUSDT",
  "tradingViewInterval": "60",
  "tradingViewAnalysis": "1시간봉 지지선 관찰",
  "assetPriceCurrency": "USDT",
  "youtubeUrl": "https://www.youtube.com/shorts/dQw4w9WgXcQ"
}
```

- `media.type`: `IMAGE` 또는 `VIDEO`
- `media.url`, `thumbnailUrl`: 업로드 API가 반환한 경로 또는 사용자 정보가 없는 HTTPS URL
- `tradingViewInterval`: `1`, `3`, `5`, `15`, `30`, `45`, `60`, `120`, `180`, `240`, `D`, `W`, `M`
- `assetPriceCurrency`: 조회 통화이며 기본값은 `USD`입니다. 호환성을 위해 기존
  `assetPrice` 필드는 당분간 받을 수 있지만 값은 사용하지 않습니다. 서버가 CoinGecko
  `/simple/price`에서 현재 가격과 공급자 갱신 시각을 조회합니다.
- `youtubeUrl`: YouTube watch, `youtu.be`, Shorts URL을 지원합니다. 서버가 영상 ID와
  `i.ytimg.com` 썸네일 URL을 파생합니다.

주요 응답 형태는 다음과 같습니다.

```json
{
  "id": 42,
  "coinSymbol": "ETH",
  "title": "ETH 1시간봉 분석",
  "content": "지지 구간을 확인했습니다.",
  "author": { "id": 7, "nickname": "작성자", "avatarColor": "#...", "walletAddress": null },
  "verifiedHolder": false,
  "assetValueKrw": null,
  "assetDisplay": "자산 비공개",
  "likes": 0,
  "comments": 0,
  "liked": false,
  "createdAt": "2026-08-31T14:00:00Z",
  "updatedAt": "2026-09-01T10:00:00Z",
  "media": [{ "id": 1, "type": "IMAGE", "url": "/api/v1/media/...png", "thumbnailUrl": null, "order": 0 }],
  "tradingView": { "symbol": "BINANCE:ETHUSDT", "interval": "60", "analysis": "1시간봉 지지선 관찰" },
  "priceSnapshot": { "price": 4321.25000000, "currency": "USDT", "capturedAt": "2026-09-01T09:59:40Z", "source": "COINGECKO" },
  "youtube": { "url": "https://www.youtube.com/shorts/dQw4w9WgXcQ", "videoId": "dQw4w9WgXcQ", "thumbnailUrl": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg" },
  "reposts": 0,
  "reposted": false,
  "bookmarked": false
}
```

좋아요·재게시·북마크·팔로우의 POST는 같은 요청을 반복해도 중복 생성되지 않으며,
DELETE도 이미 취소된 상태에서 안전하게 반복할 수 있습니다.

## 글과 댓글 수정

`PUT /posts/{postId}`는 제목, 내용, 미디어 및 분석 메타데이터를 전체 교체합니다. `coinSymbol`은
변경하지 않습니다. `media`를 생략하거나 빈 배열로 보내면 기존 미디어가 제거됩니다. 수정 시
관련 자산 가격도 다시 조회하고 `updatedAt`을 갱신합니다.

```json
{
  "title": "수정된 ETH 분석",
  "content": "수정된 내용입니다.",
  "media": [],
  "tradingViewSymbol": "BINANCE:ETHUSDT",
  "tradingViewInterval": "60",
  "tradingViewAnalysis": "수정된 분석",
  "assetPriceCurrency": "KRW",
  "youtubeUrl": null
}
```

댓글은 `PATCH /comments/{commentId}`에 `{"content":"수정 내용"}`을 보내 수정합니다. 글과 댓글
모두 작성자만 수정할 수 있으며 다른 사용자는 `403 Forbidden`을 받습니다.

## 실시간 가격

`GET /market/prices/ETH?currency=KRW`는 서버가 현재 가격을 조회해 다음 형태로 반환합니다.

```json
{
  "symbol": "ETH",
  "price": 4321000.50,
  "currency": "KRW",
  "capturedAt": "2026-09-01T09:59:40Z",
  "source": "COINGECKO"
}
```

가격은 20초간 서버 캐시를 사용합니다. 공급자 장애나 rate limit으로 조회하지 못하면
`503 Service Unavailable`을 반환하며 가격이 없는 게시글을 저장하지 않습니다. 운영 환경은
`MARKET_PRICE_BASE_URL`로 공급자 호스트를 교체할 수 있습니다. 기본 구현은
[CoinGecko Keyless Public API](https://docs.coingecko.com/docs/keyless-public-api)의
[`/simple/price`](https://docs.coingecko.com/reference/simple-price)를 사용합니다.
