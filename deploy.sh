#!/usr/bin/env bash
set -Eeuo pipefail

REPOSITORY_URL="${CRYPTALK_REPOSITORY_URL:-https://github.com/ghwns9652/cryptalk.git}"
DEPLOY_BRANCH="${CRYPTALK_BRANCH:-main}"
DEPLOY_DIRECTORY="${CRYPTALK_DEPLOY_DIRECTORY:-$PWD/cryptalk-deploy}"

if [[ -d "$DEPLOY_DIRECTORY/.git" ]]; then
  git -C "$DEPLOY_DIRECTORY" fetch origin "$DEPLOY_BRANCH"
  git -C "$DEPLOY_DIRECTORY" checkout "$DEPLOY_BRANCH"
  git -C "$DEPLOY_DIRECTORY" pull --ff-only origin "$DEPLOY_BRANCH"
elif [[ -e "$DEPLOY_DIRECTORY" ]]; then
  echo "Deployment path exists but is not a Git checkout: $DEPLOY_DIRECTORY" >&2
  exit 1
else
  git clone --branch "$DEPLOY_BRANCH" --single-branch "$REPOSITORY_URL" "$DEPLOY_DIRECTORY"
fi

if [[ ! -f "$DEPLOY_DIRECTORY/.env" ]]; then
  cp "$DEPLOY_DIRECTORY/.env.example" "$DEPLOY_DIRECTORY/.env"
  echo "Created $DEPLOY_DIRECTORY/.env. Replace every placeholder secret, then run this script again." >&2
  exit 2
fi

if grep -q 'replace-with-' "$DEPLOY_DIRECTORY/.env"; then
  echo "Refusing to deploy with placeholder secrets in $DEPLOY_DIRECTORY/.env" >&2
  exit 3
fi

docker compose --project-directory "$DEPLOY_DIRECTORY" pull mysql
docker compose --project-directory "$DEPLOY_DIRECTORY" build --pull backend
docker compose --project-directory "$DEPLOY_DIRECTORY" up -d --remove-orphans
docker compose --project-directory "$DEPLOY_DIRECTORY" ps
