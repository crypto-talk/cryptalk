import type { TickerItem } from "../../../lib/mock/landing";

type Props = {
  items: TickerItem[];
};

/**
 * 상단 흐르는 띠. 끊김 없이 돌리려고 같은 묶음을 두 번 깔고 -50%까지 이동시킵니다.
 * 두 번째 묶음은 스크린리더가 두 번 읽지 않도록 aria-hidden 처리합니다.
 */
export default function Ticker({ items }: Props) {
  const group = (hidden: boolean) => (
    <div className="hd-ticker-group" aria-hidden={hidden || undefined}>
      {items.map((item, index) => (
        <div key={index}>
          {item.lead ? <span className="hd-strong">{item.lead}</span> : null}
          {item.text}
          {item.value ? <span className="hd-strong hd-num">{item.value}</span> : null}
        </div>
      ))}
    </div>
  );

  return (
    <div className="hd-ticker">
      <div className="hd-ticker-chip">지금</div>
      <div className="hd-ticker-viewport">
        <div className="hd-ticker-track">
          {group(false)}
          {group(true)}
        </div>
      </div>
    </div>
  );
}
