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
  assert.match(html, /CRYPTALK/);
  assert.match(html, /Ethereum/);
  assert.match(html, /회원가입하고 커뮤니티에 참여하세요/);
  assert.match(html, /로그인/);
  assert.match(html, /모바일 주요 메뉴/);
  assert.match(html, /내 자산 요약/);
  assert.doesNotMatch(html, /Your site is taking shape/);
});
