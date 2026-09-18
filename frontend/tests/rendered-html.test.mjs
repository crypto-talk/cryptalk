import assert from "node:assert/strict";
import test from "node:test";
import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";
import { dirname, resolve } from "node:path";

const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const port = 3100 + (process.pid % 400);
const origin = `http://127.0.0.1:${port}`;

async function waitForServer(timeoutMs = 90_000) {
  const deadline = Date.now() + timeoutMs;
  while (Date.now() < deadline) {
    try {
      const response = await fetch(origin, { headers: { accept: "text/html" } });
      if (response.status < 500) return;
    } catch {
      // Server is not accepting connections yet.
    }
    await new Promise((done) => setTimeout(done, 500));
  }
  throw new Error(`next start did not become ready on ${origin}`);
}

test("server-renders the CrypTalk application", async (t) => {
  const server = spawn(
    process.platform === "win32" ? "npx.cmd" : "npx",
    ["next", "start", "--port", String(port)],
    { cwd: projectRoot, stdio: "ignore", env: { ...process.env, PORT: String(port) } },
  );
  t.after(() => server.kill("SIGTERM"));

  await waitForServer();

  const response = await fetch(origin, { headers: { accept: "text/html" } });
  assert.equal(response.status, 200);

  const html = await response.text();
  // 아래 문구는 현재 랜딩(app/page.tsx)의 마크업 기준이다.
  // 랜딩이 app/(shell)/page.tsx 로 옮겨가는 2단계에서 같이 손봐야 한다.
  assert.match(html, /CRYPTALK/);
  assert.match(html, /Hodlit/);
  assert.match(html, /로그인/);
  assert.match(html, /지금 뜨는 방/);
  assert.match(html, /이더리움/);
  assert.match(html, /지갑 연결/);
  assert.doesNotMatch(html, /Your site is taking shape/);

  // 폰트는 셀프호스팅이다(D-5). CDN 링크가 다시 들어오면 여기서 걸린다.
  assert.doesNotMatch(html, /cdn\.jsdelivr\.net/);
  assert.doesNotMatch(html, /fonts\.googleapis\.com/);
});
