import assert from "node:assert/strict";
import test from "node:test";

async function render() {
  const workerUrl = new URL("../dist/server/index.js", import.meta.url);
  workerUrl.searchParams.set("test", `${process.pid}-${Date.now()}`);
  const { default: worker } = await import(workerUrl.href);
  return worker.fetch(new Request("http://localhost/", { headers: { accept: "text/html" } }), {
    ASSETS: { fetch: async () => new Response("Not found", { status: 404 }) },
  }, { waitUntil() {}, passThroughOnException() {} });
}

test("server-renders the CrypTalk application", async () => {
  const response = await render();
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
