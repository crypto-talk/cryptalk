--liquibase formatted sql

--changeset cryptalk:009 context:@mock
--comment: Optional, non-login demo community data for local API development
INSERT INTO members (login_id, email, password_hash, nickname, avatar_color, asset_visibility, created_at, updated_at) VALUES
(NULL, NULL, NULL, '이더고래·데모', '#627eea', 'EXACT', '2026-09-01 08:00:00', '2026-09-01 08:00:00'),
(NULL, NULL, NULL, '디파이빌더·데모', '#7b61ff', 'RANGE', '2026-09-01 08:05:00', '2026-09-01 08:05:00'),
(NULL, NULL, NULL, '차트리더·데모', '#f7931a', 'EXACT', '2026-09-01 08:10:00', '2026-09-01 08:10:00'),
(NULL, NULL, NULL, '장기투자자·데모', '#14f195', 'HIDDEN', '2026-09-01 08:15:00', '2026-09-01 08:15:00');

INSERT INTO asset_snapshots (
    member_id, coin_id, quantity, value_krw, verified, verification_status,
    captured_at, wallet_count, holding_since, block_number, sync_status
) VALUES
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 12.750000000000000000, 73440000.00, TRUE, 'VERIFIED', '2026-09-08 08:59:30', 2, '2023-03-15 00:00:00', 23299100, 'READY'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 0.820000000000000000, 4723200.00, TRUE, 'VERIFIED', '2026-09-08 04:59:30', 1, '2025-01-10 00:00:00', 23298900, 'READY');

INSERT INTO posts (
    member_id, coin_id, title, content, author_asset_value_krw, author_verified,
    tradingview_symbol, tradingview_interval, tradingview_analysis,
    asset_price, asset_price_currency, asset_price_at, asset_price_source,
    youtube_url, youtube_video_id, youtube_thumbnail_url, created_at, updated_at
) VALUES
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 'ETH 4시간봉, 눌림목 구간을 보고 있습니다',
 '거래량이 줄어드는 조정이라 추세 훼손보다는 재진입 구간에 가깝다고 봅니다. 직전 저점 아래에서는 관점을 다시 확인할 예정입니다.',
 73440000.00, TRUE, 'BINANCE:ETHUSDT', '240', '직전 고점 돌파 후 지지 전환 여부 관찰',
 4520.25000000, 'USD', '2026-09-08 08:59:40', 'COINGECKO',
 NULL, NULL, NULL, '2026-09-08 09:00:00', '2026-09-08 09:00:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 '스테이킹 비율 상승이 공급에 미치는 영향',
 '거래소 유통 물량과 스테이킹 대기열을 함께 보면 단기 가격보다 중기 공급 변화가 더 중요한 구간 같습니다.',
 NULL, FALSE, 'BINANCE:ETHUSDT', 'D', '일봉 기준 20일 이동평균선 부근 확인',
 4508.10000000, 'USD', '2026-09-08 08:14:40', 'COINGECKO',
 NULL, NULL, NULL, '2026-09-08 08:15:00', '2026-09-08 08:15:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'BTC'),
 'BTC 주봉 종가에서 확인할 가격대',
 '주중 변동성보다 주봉 종가가 이전 저항 위에서 마감하는지 확인하고 있습니다. 레버리지는 낮게 유지하는 편이 좋아 보입니다.',
 NULL, FALSE, 'BINANCE:BTCUSDT', 'W', '이전 고점 영역의 지지 전환 확인',
 112480.00000000, 'USD', '2026-09-08 06:59:40', 'COINGECKO',
 NULL, NULL, NULL, '2026-09-08 07:00:00', '2026-09-08 07:00:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'SOL'),
 '솔라나 생태계 지표 체크리스트',
 '활성 주소만 보지 않고 수수료 매출, 스테이블코인 유동성, DEX 거래량을 같이 기록하고 있습니다. 한 지표의 급등만으로 판단하지 않는 게 핵심입니다.',
 NULL, FALSE, NULL, NULL, NULL,
 238.72000000, 'USD', '2026-09-08 05:59:40', 'COINGECKO',
 NULL, NULL, NULL, '2026-09-08 06:00:00', '2026-09-08 06:00:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 'L2 수수료 하락 이후 앱 지표를 보는 법',
 '수수료가 낮아진 뒤에는 단순 트랜잭션 수보다 반복 사용자와 프로토콜 매출의 질을 비교해야 합니다. 이번 주에는 주요 롤업을 같은 기준으로 정리해 보겠습니다.',
 4723200.00, TRUE, NULL, NULL, NULL,
 4492.90000000, 'USD', '2026-09-08 04:59:40', 'COINGECKO',
 NULL, NULL, NULL, '2026-09-08 05:00:00', '2026-09-08 05:00:00');

INSERT INTO post_holder_snapshots (
    post_id, coin_id, verification_availability, verification_level, verified_holder,
    quantity_exact, quantity_band, holding_since, holding_months, wallet_count,
    captured_at, block_number, sync_status
) VALUES
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 'SUPPORTED', 'WALLET', TRUE, 12.750000000000000000, '10~100 ETH', '2023-03-15 00:00:00', 41, 2,
 '2026-09-08 09:00:00', 23299100, 'READY'),
((SELECT id FROM posts WHERE title = '스테이킹 비율 상승이 공급에 미치는 영향'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 'SUPPORTED', 'UNVERIFIED', FALSE, NULL, NULL, NULL, NULL, 0,
 '2026-09-08 08:15:00', NULL, 'NO_DATA'),
((SELECT id FROM posts WHERE title = 'BTC 주봉 종가에서 확인할 가격대'), (SELECT id FROM coins WHERE symbol = 'BTC'),
 'NOT_SUPPORTED', NULL, FALSE, NULL, NULL, NULL, NULL, 0,
 '2026-09-08 07:00:00', NULL, 'NOT_SUPPORTED'),
((SELECT id FROM posts WHERE title = '솔라나 생태계 지표 체크리스트'), (SELECT id FROM coins WHERE symbol = 'SOL'),
 'NOT_SUPPORTED', NULL, FALSE, NULL, NULL, NULL, NULL, 0,
 '2026-09-08 06:00:00', NULL, 'NOT_SUPPORTED'),
((SELECT id FROM posts WHERE title = 'L2 수수료 하락 이후 앱 지표를 보는 법'), (SELECT id FROM coins WHERE symbol = 'ETH'),
 'SUPPORTED', 'WALLET', TRUE, 0.820000000000000000, '0.1~1 ETH', '2025-01-10 00:00:00', 19, 1,
 '2026-09-08 05:00:00', 23298900, 'READY');

INSERT INTO comments (post_id, member_id, content, created_at, updated_at) VALUES
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'),
 (SELECT id FROM members WHERE nickname = '디파이빌더·데모'), 'L2 예치 자산 흐름도 같은 방향인지 같이 보겠습니다.', '2026-09-08 09:12:00', '2026-09-08 09:12:00'),
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'),
 (SELECT id FROM members WHERE nickname = '차트리더·데모'), '거래량 기준을 어느 거래소로 잡으셨는지도 궁금합니다.', '2026-09-08 09:18:00', '2026-09-08 09:18:00'),
((SELECT id FROM posts WHERE title = 'BTC 주봉 종가에서 확인할 가격대'),
 (SELECT id FROM members WHERE nickname = '장기투자자·데모'), '주봉 마감 후 온체인 유입량과 함께 비교해 볼게요.', '2026-09-08 07:20:00', '2026-09-08 07:20:00'),
((SELECT id FROM posts WHERE title = 'L2 수수료 하락 이후 앱 지표를 보는 법'),
 (SELECT id FROM members WHERE nickname = '이더고래·데모'), '반복 사용자 비율을 주 단위로 보면 노이즈가 줄더라고요.', '2026-09-08 05:16:00', '2026-09-08 05:16:00');

INSERT INTO comment_holder_snapshots (
    comment_id, coin_id, verification_availability, verification_level, verified_holder,
    quantity_exact, quantity_band, holding_since, holding_months, wallet_count,
    captured_at, block_number, sync_status
) VALUES
((SELECT c.id FROM comments c JOIN members m ON m.id = c.member_id WHERE c.content = 'L2 예치 자산 흐름도 같은 방향인지 같이 보겠습니다.' AND m.nickname = '디파이빌더·데모'),
 (SELECT id FROM coins WHERE symbol = 'ETH'), 'SUPPORTED', 'WALLET', TRUE,
 0.820000000000000000, '0.1~1 ETH', '2025-01-10 00:00:00', 19, 1, '2026-09-08 09:12:00', 23299150, 'READY'),
((SELECT c.id FROM comments c JOIN members m ON m.id = c.member_id WHERE c.content = '거래량 기준을 어느 거래소로 잡으셨는지도 궁금합니다.' AND m.nickname = '차트리더·데모'),
 (SELECT id FROM coins WHERE symbol = 'ETH'), 'SUPPORTED', 'UNVERIFIED', FALSE,
 NULL, NULL, NULL, NULL, 0, '2026-09-08 09:18:00', NULL, 'NO_DATA'),
((SELECT c.id FROM comments c JOIN members m ON m.id = c.member_id WHERE c.content = '주봉 마감 후 온체인 유입량과 함께 비교해 볼게요.' AND m.nickname = '장기투자자·데모'),
 (SELECT id FROM coins WHERE symbol = 'BTC'), 'NOT_SUPPORTED', NULL, FALSE,
 NULL, NULL, NULL, NULL, 0, '2026-09-08 07:20:00', NULL, 'NOT_SUPPORTED'),
((SELECT c.id FROM comments c JOIN members m ON m.id = c.member_id WHERE c.content = '반복 사용자 비율을 주 단위로 보면 노이즈가 줄더라고요.' AND m.nickname = '이더고래·데모'),
 (SELECT id FROM coins WHERE symbol = 'ETH'), 'SUPPORTED', 'WALLET', TRUE,
 12.750000000000000000, '10~100 ETH', '2023-03-15 00:00:00', 41, 2, '2026-09-08 05:16:00', 23298950, 'READY');

INSERT INTO post_likes (post_id, member_id, created_at) VALUES
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'), (SELECT id FROM members WHERE nickname = '디파이빌더·데모'), '2026-09-08 09:10:00'),
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'), (SELECT id FROM members WHERE nickname = '차트리더·데모'), '2026-09-08 09:11:00'),
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'), (SELECT id FROM members WHERE nickname = '장기투자자·데모'), '2026-09-08 09:13:00'),
((SELECT id FROM posts WHERE title = '스테이킹 비율 상승이 공급에 미치는 영향'), (SELECT id FROM members WHERE nickname = '이더고래·데모'), '2026-09-08 08:35:00'),
((SELECT id FROM posts WHERE title = 'BTC 주봉 종가에서 확인할 가격대'), (SELECT id FROM members WHERE nickname = '장기투자자·데모'), '2026-09-08 07:25:00'),
((SELECT id FROM posts WHERE title = 'L2 수수료 하락 이후 앱 지표를 보는 법'), (SELECT id FROM members WHERE nickname = '이더고래·데모'), '2026-09-08 05:20:00');

INSERT INTO post_reposts (post_id, member_id, created_at) VALUES
((SELECT id FROM posts WHERE title = 'ETH 4시간봉, 눌림목 구간을 보고 있습니다'), (SELECT id FROM members WHERE nickname = '장기투자자·데모'), '2026-09-08 09:25:00'),
((SELECT id FROM posts WHERE title = 'BTC 주봉 종가에서 확인할 가격대'), (SELECT id FROM members WHERE nickname = '이더고래·데모'), '2026-09-08 07:30:00');

INSERT INTO member_follows (follower_id, following_id, created_at) VALUES
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM members WHERE nickname = '이더고래·데모'), '2026-09-02 10:00:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM members WHERE nickname = '디파이빌더·데모'), '2026-09-02 10:05:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM members WHERE nickname = '차트리더·데모'), '2026-09-02 10:10:00');
