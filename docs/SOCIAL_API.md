# SNS API 명세

기본 경로는 `/api/v1`입니다. Swagger UI는 배포 서버의 `/swagger-ui.html`, OpenAPI JSON은
`/v3/api-docs`에서 확인할 수 있습니다. 변경 API는 `Authorization: Bearer {accessToken}`이
필요하고, 피드·게시글·댓글·팔로우 목록 조회는 로그인 없이도 사용할 수 있습니다.

## 엔드포인트

| 기능 | Method | Path | 인증 |
|---|---|---|---|
| 랜딩 피드 | GET | `/feed?size=30` | 선택 |
| 코인별 글 | GET | `/communities/{symbol}/posts?size=30` | 선택 |
| 글 상세 | GET | `/posts/{postId}` | 선택 |
| 글 작성/삭제 | POST / DELETE | `/posts`, `/posts/{postId}` | 필요 |
| 미디어 업로드/조회 | POST / GET | `/media`, `/media/{fileName}` | 업로드만 필요 |
| 댓글 목록/작성 | GET / POST | `/posts/{postId}/comments` | 작성만 필요 |
| 댓글 삭제 | DELETE | `/comments/{commentId}` | 필요, 작성자만 |
| 좋아요 | POST / DELETE | `/posts/{postId}/likes` | 필요 |
| 재게시 | POST / DELETE | `/posts/{postId}/reposts` | 필요 |
| 북마크 | POST / DELETE | `/posts/{postId}/bookmarks` | 필요 |
| 내 북마크 | GET | `/me/bookmarks` | 필요 |
| 팔로우/취소 | POST / DELETE | `/members/{memberId}/follow` | 필요 |
| 팔로우 통계 | GET | `/members/{memberId}/social` | 선택 |
| 팔로워/팔로잉 목록 | GET | `/members/{memberId}/followers`, `/following` | 불필요 |

조회 응답의 `liked`, `reposted`, `bookmarked`, `followedByMe`는 로그인 사용자를 기준으로
계산됩니다. 비로그인 조회에서는 `false`입니다. `size`는 1~100 범위로 보정됩니다.

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
  "assetPrice": 4321.25,
  "assetPriceCurrency": "USDT",
  "youtubeUrl": "https://www.youtube.com/shorts/dQw4w9WgXcQ"
}
```

- `media.type`: `IMAGE` 또는 `VIDEO`
- `media.url`, `thumbnailUrl`: 업로드 API가 반환한 경로 또는 사용자 정보가 없는 HTTPS URL
- `tradingViewInterval`: `1`, `3`, `5`, `15`, `30`, `45`, `60`, `120`, `180`, `240`, `D`, `W`, `M`
- `assetPrice`와 `assetPriceCurrency`: 둘 다 입력하거나 둘 다 생략합니다. 서버가 저장 시각을
  `priceSnapshot.capturedAt`에 기록합니다. 이 값은 클라이언트가 제출한 표시용 스냅샷이며,
  서버가 거래소 시세를 검증한 값은 아닙니다.
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
  "media": [{ "id": 1, "type": "IMAGE", "url": "/api/v1/media/...png", "thumbnailUrl": null, "order": 0 }],
  "tradingView": { "symbol": "BINANCE:ETHUSDT", "interval": "60", "analysis": "1시간봉 지지선 관찰" },
  "priceSnapshot": { "price": 4321.25000000, "currency": "USDT", "capturedAt": "2026-08-31T14:00:00Z" },
  "youtube": { "url": "https://www.youtube.com/shorts/dQw4w9WgXcQ", "videoId": "dQw4w9WgXcQ", "thumbnailUrl": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg" },
  "reposts": 0,
  "reposted": false,
  "bookmarked": false
}
```

좋아요·재게시·북마크·팔로우의 POST는 같은 요청을 반복해도 중복 생성되지 않으며,
DELETE도 이미 취소된 상태에서 안전하게 반복할 수 있습니다.
