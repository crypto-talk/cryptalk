# CrypTalk frontend agent harness

Read the repository `AGENTS.md` first. This file adds the rules that apply under `frontend/`.

## Stack

- Next.js 16 App Router, React 19, TypeScript strict, Tailwind CSS 4.
- UI primitives: shadcn/ui (copied into `components/ui/`, styled with our tokens). Icons: `lucide-react`.
- Server state: TanStack Query. Client global state: session, modal, toast only.
- Editor: Tiptap (rich text, markdown paste, custom chart node). Charts: `lightweight-charts`.
- Tests: Vitest for unit tests (`pnpm test`), Playwright for e2e. `tests/rendered-html.test.mjs`
  is a separate build-dependent check run by `pnpm run test:html`.
- Fonts: Pretendard, self-hosted. Static subset woff2 in `styles/fonts/`, loaded by `app/fonts.ts`.
- pnpm is the package manager, pinned by `packageManager` in `package.json`. Never run
  `npm install` here; it would create a competing `package-lock.json`.
- Deployed on Vercel. `next build` is the build; there is no Cloudflare Worker runtime.
- The API client talks to the Spring Boot backend at `NEXT_PUBLIC_API_URL`. There is no
  database, ORM or server-side data layer in this project.

## Repository map (settled 2026-09-18)

The full tree with rationale lives in the Notion page "구조 고민하기" → "확정 폴더 트리".
Keep this map and that page in sync.

```
app/
  layout.tsx            root: font + <Providers>. Server component.
  providers.tsx         "use client": QueryClientProvider (toast provider goes here too)
  fonts.ts              next/font/local; files in styles/fonts/
  globals.css           imports styles/tokens.css + Tailwind @theme mapping only
  not-found.tsx         single 404
  (shell)/              header + sidebar (>=900px) / bottom tab (<900px)
    page.tsx            landing (server component, Promise.all over sections)
    subhodl/[symbol]/page.tsx             room board
    subhodl/[symbol]/[postId]/page.tsx    post detail
    [handle]/page.tsx   public profile; notFound() unless handle starts with "@"
  (focus)/              header only
    login/  signup/  write/  settings/  settings/wallet/
    subhodl/[symbol]/[postId]/edit/
features/               one folder per domain: auth wallet room post comment vote badge landing
  <domain>/components/  hooks/  api.ts  types.ts  mock.ts  (schema.ts optional)
                        unit tests sit next to the source as `*.test.ts`
components/ui/          shadcn primitives (+ *.stories.tsx)
components/layout/      header sidebar bottom-tab footer fab
lib/http.ts             fetch wrapper: baseURL, credentials, cookie forwarding on the server,
                        {code,message} normalisation, 401 refresh
lib/query.ts  lib/format/  lib/utils.ts (cn)  lib/config.ts (env, single read point)
styles/tokens.css       semantic CSS variables; [data-theme="dark"] block left empty
styles/fonts/           Pretendard subset woff2, 400/600/700/800/900
components.json         shadcn/ui config; `shadcn add` writes into components/ui/
vitest.config.ts        unit test scope: features/**/*.test.ts, lib/**/*.test.ts
tests/e2e/              Playwright: login.spec.ts, publish-post.spec.ts (run locally)
.storybook/             Storybook config; stories only for components/ui and features/badge
```

Existing `app/_components/*`, `lib/api.ts`, `lib/AuthApi.ts`, `lib/mock/landing.ts` are the
pre-restructure layout. Move them into the tree above; do not add new files to the old locations.

## Structure rules

1. Imports flow one way: `app/ -> features/ -> components/ | lib/`. `features/*` never import
   each other. Shared code moves down to `components/` or `lib/`. Enforced by ESLint.
2. `features/<domain>/api.ts` is the only data entry point. Mock data lives in `mock.ts` and
   never leaks past `api.ts`. Components receive data through props only.
3. Badge tier, holding period and amount-range formatting exist only in `features/badge/`.
   These functions implement the wallet de-anonymisation guard (month rounding, wide ranges,
   no sell timestamps). Do not reimplement them elsewhere.
4. One component per file. No barrel `index.ts` files; use the `@/` alias instead.
5. File and folder names are kebab-case (`post-card.tsx`). Components and types are PascalCase.
   Hooks are `use-*.ts`. Windows git is case-insensitive and the Vercel build is not.
6. `"use client"` goes on the interactive leaf (button, tab, editor, vote widget), not on a
   whole section or page. The root layout stays a server component; context providers live
   in `app/providers.tsx` and are rendered from it.
7. Design tokens are defined once in `styles/tokens.css` with semantic names
   (`--color-text-primary`, not `--purple`). Components use Tailwind classes that reference
   them. Runtime values such as a room's `accentColor` are injected as inline CSS variables.
8. Tab, filter and sort state of any list lives in the URL query string.
9. Loading/error boundaries: `loading.tsx` per route segment, `error.tsx` per route group,
   inline retry inside a failed section. Toasts and modals are for action failures only.

## Restructure state

Step 1 (foundation, no screens) is done: tokens, self-hosted Pretendard, `components/ui/`
primitives, `features/badge/` with unit tests, `lib/` (config, format, query, http skeleton,
utils), route group boundaries with `error.tsx`, the root `not-found.tsx`, and the ESLint
import-direction rule.

Deliberately not done yet, do not treat these as oversights:

- `app/globals.css` still carries the pre-restructure landing rules. Its ~95 classes back the
  current `app/page.tsx`, so it is dismantled when the landing markup moves to
  `app/(shell)/page.tsx` in step 2.
- `lib/http.ts` stops at baseURL, `credentials: 'include'` and `{code,message}` normalisation.
  Token handling and the 401 refresh wait for the backend cookie change (B-3) and the agreed
  error shape (C-5). Requests that need auth still go through the old `lib/api.ts`.
- `components/ui/` is exempt from ESLint so the shadcn copies stay diffable against upstream.
- Storybook. It goes in now that `components/ui/` and `features/badge/` exist.
- husky, lint-staged and GitHub Actions. They live at the repository root, outside
  `frontend/`, so they need an explicit scope expansion and belong in their own change.

Step 2 moves the landing into `app/(shell)/page.tsx`, splits `app/_components/*` into
`features/*` and `components/layout/`, and updates the assertions in
`tests/rendered-html.test.mjs`, which are pinned to the current landing markup.

## Working rules

- Base new work on the latest `origin/develop` and work on a `feature/...` branch.
- Keep backend changes out of frontend tasks unless the user explicitly expands the scope.
- Preserve unrelated user changes. Never stash, reset, or delete them to make a task easier.
- Never commit `.env*` files, tokens, or API credentials.
- Use Conventional Commit subjects: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`.

## Never minify or collapse source

Write one statement per line. Do not collapse a component, a rule set, or a JSX subtree
onto a single line to save space. (`app/globals.css` still packs ~22 KB onto 55 lines; it is
dismantled into `styles/tokens.css` + Tailwind classes during the restructure.)

## Contract with the backend

- `features/*/types.ts` mirror the backend DTOs field for field. When an endpoint's response
  changes, update the type in the same commit as the code that reads it.
- Runtime response validation (zod) only for money/PII responses: `/me/assets`, snapshots,
  wallet. Everything else trusts the type.
- `GET /api/v1/me/assets` returns `AssetPortfolio` (`{ walletCount, assets }`), not an array.
- `holdingMonths` is always `null` until an EVM indexer populates `holdingSince`. Render the
  fixed label `보유 기간 미확인`; do not build UI that assumes a value.
- Public post and comment responses do not carry wallet addresses. Do not reintroduce them.
- `POST /posts` ignores a client-supplied `assetPrice`; the server captures the price itself.
- Auth: email/password. `refresh` is an httpOnly cookie today; `access` is in sessionStorage
  and is being moved to an httpOnly cookie (backend task). Until then, server components
  cannot render logged-in state.
- Open backend requests are listed in the Notion page "구조 고민하기" → "백엔드와 논의할 항목".

## Fonts

Pretendard is self-hosted through `next/font/local` (subset woff2). Do not load fonts from
Google Fonts or a CDN `<link>`; the CDN link currently in `app/page.tsx` is removed in the
restructure.

## Verification

Run from `frontend/`, in order:

1. `pnpm exec tsc --noEmit`
2. `pnpm run lint`
3. `pnpm test` — Vitest unit suite, no build needed
4. `pnpm run build`
5. `pnpm run test:html` — rendered-HTML check; it builds again, so run it after step 4 only
   when the change can affect the rendered page

CI runs steps 1-3. Do not put `test:html` in CI as well; it would make the build run twice.

Playwright e2e tests need the backend running; they are run locally, not in CI, for now.
