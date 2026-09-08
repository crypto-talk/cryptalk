# CrypTalk frontend agent harness

Read the repository `AGENTS.md` first. This file adds the rules that apply under `frontend/`.

## Stack

- Next.js 16 App Router, React 19, TypeScript strict, Tailwind CSS 4.
- Deployed on Vercel. `next build` is the build; there is no Cloudflare Worker runtime.
- The API client talks to the Spring Boot backend at `NEXT_PUBLIC_API_URL`. There is no
  database, ORM or server-side data layer in this project.

## Repository map

- `app/`: App Router routes, layouts and global styles.
- `lib/`: API client and browser-side helpers.
- `tests/`: Node test runner suites executed against a built app.

The directory layout beyond this is not settled. Do not invent a structure without asking.

## Working rules

- Base new work on the latest `origin/develop` and work on a `feature/...` branch.
- Keep backend changes out of frontend tasks unless the user explicitly expands the scope.
- Preserve unrelated user changes. Never stash, reset, or delete them to make a task easier.
- Never commit `.env*` files, tokens, or API credentials.
- Use Conventional Commit subjects: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`.

## Never minify or collapse source

`app/page.tsx` and `app/globals.css` currently pack roughly 22 KB onto 204 and 55 lines.
Any two concurrent edits land on the same line and conflict, and no diff is reviewable.

Write one statement per line. Do not collapse a component, a rule set, or a JSX subtree
onto a single line to save space.

## Contract with the backend

- `lib/api.ts` types mirror the backend DTOs. When an endpoint's response changes, update the
  type in the same commit as the code that reads it.
- `GET /api/v1/me/assets` returns `AssetPortfolio` (`{ walletCount, assets }`), not an array.
- `holdingMonths` is always `null` until an EVM indexer populates `holdingSince`. Do not build
  UI that assumes a value.
- Public post and comment responses do not carry wallet addresses. Do not reintroduce them.
- `POST /posts` ignores a client-supplied `assetPrice`; the server captures the price itself.

## Verification

Run from `frontend/`, in order:

1. `npx tsc --noEmit`
2. `npm run lint`
3. `npm run build`
4. `npm test`

`next/font/google` fetches from Google Fonts at build time, so `npm run build` and `npm test`
need network access to `fonts.googleapis.com`. In a sandbox without it, say so rather than
reporting the build as passing.
