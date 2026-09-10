--liquibase formatted sql

--changeset cryptalk:010 context:@mock
--comment: Expand optional demo content across every supported community
INSERT INTO posts (
    member_id, coin_id, title, content, author_asset_value_krw, author_verified,
    asset_price, asset_price_currency, asset_price_at, asset_price_source, created_at, updated_at
) VALUES
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'BTC'), '비트코인 단기 과열을 판단하는 세 가지 기준', '펀딩비, 미결제약정, 현물 프리미엄을 함께 보고 있습니다. 하나만 과열됐을 때보다 세 지표가 동시에 올라올 때 포지션 크기를 줄이는 편입니다.', NULL, FALSE, 113240.00000000, 'USD', '2026-09-09 09:29:40', 'COINGECKO', '2026-09-09 09:30:00', '2026-09-09 09:30:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'), '이더리움 장기 보유자가 보는 이번 분기 핵심', '가격보다 스테이블코인 공급과 네트워크 수수료 회복을 먼저 확인하고 있습니다. 두 지표가 같이 개선되면 생태계 활동의 질도 좋아질 가능성이 큽니다.', NULL, FALSE, 4568.20000000, 'USD', '2026-09-09 09:14:40', 'COINGECKO', '2026-09-09 09:15:00', '2026-09-09 09:15:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'SOL'), 'SOL 생태계 거래량을 볼 때 주의할 점', '봇 거래가 섞일 수 있어 총 거래량과 함께 순사용자 수, 평균 거래 크기, 수수료 매출을 비교하고 있습니다.', NULL, FALSE, 241.85000000, 'USD', '2026-09-09 08:59:40', 'COINGECKO', '2026-09-09 09:00:00', '2026-09-09 09:00:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'XRP'), 'XRP 결제 생태계 뉴스 정리', '파트너십 발표와 실제 결제량 증가는 구분해서 볼 필요가 있습니다. 이번 달 공개 지표를 기준으로 실제 사용량 변화를 정리했습니다.', NULL, FALSE, 3.12000000, 'USD', '2026-09-09 08:44:40', 'COINGECKO', '2026-09-09 08:45:00', '2026-09-09 08:45:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'DOGE'), 'DOGE 거래량 급증 구간 복기', '가격 돌파 전에 현물 거래량이 먼저 늘었지만 파생시장 미결제약정도 빠르게 따라붙었습니다. 변동성 확대에 대비할 구간입니다.', NULL, FALSE, 0.28500000, 'USD', '2026-09-09 08:29:40', 'COINGECKO', '2026-09-09 08:30:00', '2026-09-09 08:30:00'),
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'ADA'), '카르다노 거버넌스 업데이트 요약', '최근 거버넌스 제안의 핵심 쟁점과 투표 일정을 정리했습니다. 기술 로드맵과 재단 예산이 실제 개발 활동으로 이어지는지 확인하려고 합니다.', NULL, FALSE, 0.92000000, 'USD', '2026-09-09 08:14:40', 'COINGECKO', '2026-09-09 08:15:00', '2026-09-09 08:15:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'BNB'), 'BNB 체인 디파이 유동성 체크', '상위 프로토콜에 집중된 유동성과 신규 풀의 인센티브 의존도를 비교했습니다. 보상 종료 뒤에도 예치가 유지되는지가 중요합니다.', NULL, FALSE, 928.40000000, 'USD', '2026-09-09 07:59:40', 'COINGECKO', '2026-09-09 08:00:00', '2026-09-09 08:00:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'AVAX'), 'Avalanche 서브넷 지표 월간 기록', '활성 서브넷 수보다 꾸준히 블록을 만들고 사용자를 유지하는 네트워크 비율에 집중하고 있습니다.', NULL, FALSE, 42.18000000, 'USD', '2026-09-09 07:44:40', 'COINGECKO', '2026-09-09 07:45:00', '2026-09-09 07:45:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'DOT'), 'DOT 박스권에서 확인할 거래량', '긴 박스권에서는 돌파 순간보다 돌파 뒤 되돌림의 거래량이 더 중요하다고 봅니다. 주봉 기준으로 확인 중입니다.', NULL, FALSE, 6.74000000, 'USD', '2026-09-09 07:29:40', 'COINGECKO', '2026-09-09 07:30:00', '2026-09-09 07:30:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'LINK'), 'Chainlink CCIP 사용량 메모', '통합 네트워크 수와 실제 메시지 전송량을 분리해서 봤습니다. 신규 통합 발표 이후 반복 사용이 이어지는지가 핵심입니다.', NULL, FALSE, 27.35000000, 'USD', '2026-09-09 07:14:40', 'COINGECKO', '2026-09-09 07:15:00', '2026-09-09 07:15:00'),
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'POL'), 'Polygon 생태계 마이그레이션 현황', '토큰 전환율과 주요 앱의 지원 현황을 정리했습니다. 브리지 유동성 이동이 안정화되는지도 함께 보고 있습니다.', NULL, FALSE, 0.48500000, 'USD', '2026-09-09 06:59:40', 'COINGECKO', '2026-09-09 07:00:00', '2026-09-09 07:00:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'TON'), 'TON 미니앱 사용 지표 살펴보기', '가입자 수보다 월간 반복 사용자와 결제 전환율을 중심으로 봤습니다. 이벤트 종료 후 잔존율도 계속 기록할 생각입니다.', NULL, FALSE, 4.82000000, 'USD', '2026-09-09 06:44:40', 'COINGECKO', '2026-09-09 06:45:00', '2026-09-09 06:45:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'TRX'), 'TRON 스테이블코인 흐름 주간 정리', '발행량과 전송량이 동시에 늘었지만 거래소 이동 비중도 커졌습니다. 사용자 결제와 자금 이동을 나눠 볼 필요가 있습니다.', NULL, FALSE, 0.34200000, 'USD', '2026-09-09 06:29:40', 'COINGECKO', '2026-09-09 06:30:00', '2026-09-09 06:30:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'LTC'), '라이트코인 반감기 이후 장기 차트', '해시레이트와 채굴자 매도 압력을 가격 구조와 함께 비교했습니다. 거래량이 붙기 전까지는 범위 대응 관점입니다.', NULL, FALSE, 128.60000000, 'USD', '2026-09-09 06:14:40', 'COINGECKO', '2026-09-09 06:15:00', '2026-09-09 06:15:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'BCH'), 'BCH 결제 사용처 데이터 기록', '단순 가맹점 수 대신 실제 결제 건수와 평균 금액 변화를 찾아보고 있습니다. 공개 데이터의 한계도 같이 적었습니다.', NULL, FALSE, 684.30000000, 'USD', '2026-09-09 05:59:40', 'COINGECKO', '2026-09-09 06:00:00', '2026-09-09 06:00:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'UNI'), 'Uniswap 유동성 공급자 수익 점검', '명목 수수료보다 비영구적 손실을 반영한 순수익으로 풀을 비교해야 합니다. 변동성 구간별 결과를 정리했습니다.', NULL, FALSE, 14.72000000, 'USD', '2026-09-09 05:44:40', 'COINGECKO', '2026-09-09 05:45:00', '2026-09-09 05:45:00'),
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'AAVE'), 'Aave 대출 시장 리스크 지표', '담보 구성과 대형 계정의 청산 가격대를 함께 확인했습니다. 예치 규모가 커질수록 자산별 집중도를 꼭 봐야 합니다.', NULL, FALSE, 322.90000000, 'USD', '2026-09-09 05:29:40', 'COINGECKO', '2026-09-09 05:30:00', '2026-09-09 05:30:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'ATOM'), '코스모스 체인 간 활동 비교', 'IBC 전송량과 활성 채널 수를 월별로 비교했습니다. 일회성 에어드롭 트래픽은 따로 표시했습니다.', NULL, FALSE, 8.95000000, 'USD', '2026-09-09 05:14:40', 'COINGECKO', '2026-09-09 05:15:00', '2026-09-09 05:15:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'NEAR'), 'NEAR 일봉 추세 전환 체크', '저점은 높아지고 있지만 장기 이동평균선 저항이 남아 있습니다. 거래량을 동반한 종가 돌파를 기다리고 있습니다.', NULL, FALSE, 5.84000000, 'USD', '2026-09-09 04:59:40', 'COINGECKO', '2026-09-09 05:00:00', '2026-09-09 05:00:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'SUI'), 'Sui 오브젝트 모델 개발 후기', '공유 오브젝트를 사용할 때 생기는 병목과 설계 선택을 간단한 예제로 정리했습니다. 앱 지표를 볼 때 기술 구조도 함께 이해하면 좋습니다.', NULL, FALSE, 4.12000000, 'USD', '2026-09-09 04:44:40', 'COINGECKO', '2026-09-09 04:45:00', '2026-09-09 04:45:00'),
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'ETH'), '보유 인증 후에도 리스크 관리는 별개입니다', '장기 관점이지만 한 자산에 대한 확신과 포트폴리오 위험은 분리해서 관리합니다. 이번 달에는 현금 비중을 조금 높였습니다.', 73440000.00, TRUE, 4542.70000000, 'USD', '2026-09-09 04:29:40', 'COINGECKO', '2026-09-09 04:30:00', '2026-09-09 04:30:00'),
((SELECT id FROM members WHERE nickname = '이더고래·데모'), (SELECT id FROM coins WHERE symbol = 'BTC'), 'BTC와 ETH 비중을 조정한 이유', '최근 변동성과 생태계 촉매를 비교해 ETH 비중을 유지하고 BTC는 분할 매수 구간을 넓혔습니다.', NULL, FALSE, 112920.00000000, 'USD', '2026-09-09 04:14:40', 'COINGECKO', '2026-09-09 04:15:00', '2026-09-09 04:15:00'),
((SELECT id FROM members WHERE nickname = '차트리더·데모'), (SELECT id FROM coins WHERE symbol = 'SOL'), 'SOL 4시간봉 지지선 업데이트', '이전 돌파 구간을 다시 테스트하고 있습니다. 거래량이 유지되는 동안에는 추세 추종 관점을 유지합니다.', NULL, FALSE, 240.10000000, 'USD', '2026-09-09 03:59:40', 'COINGECKO', '2026-09-09 04:00:00', '2026-09-09 04:00:00'),
((SELECT id FROM members WHERE nickname = '장기투자자·데모'), (SELECT id FROM coins WHERE symbol = 'XRP'), 'XRP 장기 보유 논리 다시 점검하기', '처음 매수했던 근거와 지금의 네트워크 지표를 비교했습니다. 근거가 바뀌면 보유 기간과 상관없이 비중을 조정해야 합니다.', NULL, FALSE, 3.08000000, 'USD', '2026-09-09 03:44:40', 'COINGECKO', '2026-09-09 03:45:00', '2026-09-09 03:45:00'),
((SELECT id FROM members WHERE nickname = '디파이빌더·데모'), (SELECT id FROM coins WHERE symbol = 'DOGE'), 'DOGE 결제 실험 사례 모음', '밈과 가격 이야기 외에도 소액 결제에 적용된 사례를 모아봤습니다. 지속 사용 여부를 확인할 데이터가 더 필요합니다.', NULL, FALSE, 0.28100000, 'USD', '2026-09-09 03:29:40', 'COINGECKO', '2026-09-09 03:30:00', '2026-09-09 03:30:00');

INSERT INTO post_holder_snapshots (
    post_id, coin_id, verification_availability, verification_level, verified_holder,
    quantity_exact, quantity_band, holding_since, holding_months, wallet_count,
    captured_at, block_number, sync_status
)
SELECT p.id, p.coin_id, c.verification_availability,
       CASE WHEN c.verification_availability = 'SUPPORTED' AND p.author_verified = TRUE THEN 'WALLET'
            WHEN c.verification_availability = 'SUPPORTED' THEN 'UNVERIFIED' ELSE NULL END,
       p.author_verified,
       CASE WHEN p.author_verified = TRUE THEN 12.750000000000000000 ELSE NULL END,
       CASE WHEN p.author_verified = TRUE THEN '10~100 ETH' ELSE NULL END,
       CASE WHEN p.author_verified = TRUE THEN '2023-03-15 00:00:00' ELSE NULL END,
       CASE WHEN p.author_verified = TRUE THEN 41 ELSE NULL END,
       CASE WHEN p.author_verified = TRUE THEN 2 ELSE 0 END,
       p.created_at,
       CASE WHEN p.author_verified = TRUE THEN 23304100 ELSE NULL END,
       CASE WHEN c.verification_availability = 'SUPPORTED' AND p.author_verified = TRUE THEN 'READY'
            WHEN c.verification_availability = 'SUPPORTED' THEN 'NO_DATA'
            ELSE c.verification_availability END
FROM posts p
JOIN coins c ON c.id = p.coin_id
JOIN members author ON author.id = p.member_id
WHERE author.nickname IN ('이더고래·데모', '디파이빌더·데모', '차트리더·데모', '장기투자자·데모')
  AND p.created_at >= '2026-09-09 00:00:00' AND p.created_at < '2026-09-10 00:00:00';

INSERT INTO comments (post_id, member_id, content, created_at, updated_at)
SELECT p.id,
       CASE WHEN author.nickname = '이더고래·데모'
            THEN (SELECT id FROM members WHERE nickname = '디파이빌더·데모')
            ELSE (SELECT id FROM members WHERE nickname = '이더고래·데모') END,
       CONCAT(c.symbol, ' 관점 잘 읽었습니다. 다음 업데이트도 기대할게요.'),
       p.created_at, p.created_at
FROM posts p
JOIN coins c ON c.id = p.coin_id
JOIN members author ON author.id = p.member_id
WHERE author.nickname IN ('이더고래·데모', '디파이빌더·데모', '차트리더·데모', '장기투자자·데모')
  AND p.created_at >= '2026-09-09 00:00:00' AND p.created_at < '2026-09-10 00:00:00';

INSERT INTO comment_holder_snapshots (
    comment_id, coin_id, verification_availability, verification_level, verified_holder,
    quantity_exact, quantity_band, holding_since, holding_months, wallet_count,
    captured_at, block_number, sync_status
)
SELECT cm.id, p.coin_id, coin.verification_availability,
       CASE WHEN coin.verification_availability = 'SUPPORTED' THEN 'WALLET' ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' THEN TRUE ELSE FALSE END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' AND commenter.nickname = '이더고래·데모' THEN 12.750000000000000000
            WHEN coin.verification_availability = 'SUPPORTED' THEN 0.820000000000000000 ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' AND commenter.nickname = '이더고래·데모' THEN '10~100 ETH'
            WHEN coin.verification_availability = 'SUPPORTED' THEN '0.1~1 ETH' ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' AND commenter.nickname = '이더고래·데모' THEN '2023-03-15 00:00:00'
            WHEN coin.verification_availability = 'SUPPORTED' THEN '2025-01-10 00:00:00' ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' AND commenter.nickname = '이더고래·데모' THEN 41
            WHEN coin.verification_availability = 'SUPPORTED' THEN 19 ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' AND commenter.nickname = '이더고래·데모' THEN 2
            WHEN coin.verification_availability = 'SUPPORTED' THEN 1 ELSE 0 END,
       cm.created_at,
       CASE WHEN coin.verification_availability = 'SUPPORTED' THEN 23304110 ELSE NULL END,
       CASE WHEN coin.verification_availability = 'SUPPORTED' THEN 'READY' ELSE coin.verification_availability END
FROM comments cm
JOIN posts p ON p.id = cm.post_id
JOIN coins coin ON coin.id = p.coin_id
JOIN members commenter ON commenter.id = cm.member_id
JOIN members author ON author.id = p.member_id
WHERE author.nickname IN ('이더고래·데모', '디파이빌더·데모', '차트리더·데모', '장기투자자·데모')
  AND p.created_at >= '2026-09-09 00:00:00' AND p.created_at < '2026-09-10 00:00:00';

INSERT INTO post_likes (post_id, member_id, created_at)
SELECT p.id, liker.id, p.created_at
FROM posts p
JOIN members liker ON liker.nickname IN ('차트리더·데모', '장기투자자·데모') AND liker.id <> p.member_id
JOIN members author ON author.id = p.member_id
WHERE author.nickname IN ('이더고래·데모', '디파이빌더·데모', '차트리더·데모', '장기투자자·데모')
  AND p.created_at >= '2026-09-09 00:00:00' AND p.created_at < '2026-09-10 00:00:00';
