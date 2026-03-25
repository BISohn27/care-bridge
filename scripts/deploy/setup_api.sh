#!/bin/bash

set -euo pipefail

print_usage() {
  echo "usage: $0 [env_name] [branch_name]"
  echo "  env_name: 배포할 환경 이름 (예: dev, qa, prod)"
  echo "  branch_name: Git 브랜치명 (예: qa, prod, feature/deploy-test)"
  echo "  required env:"
  echo "    AWS_REGION=ap-northeast-2"
  echo "    ECR_REGISTRY=<aws-account-id>.dkr.ecr.<region>.amazonaws.com"
  echo "    ECR_REPOSITORY_NAME=<ecr-repository-name>"
}

check_required_commands() {
  local REQUIRED_COMMANDS=$1
  local MISSING_COMMANDS=()

  for cmd in $REQUIRED_COMMANDS; do
    command -v "$cmd" &> /dev/null || MISSING_COMMANDS+=("$cmd")
  done

  if [ ${#MISSING_COMMANDS[@]} -gt 0 ]; then
    echo "다음 명령어가 없습니다: ${MISSING_COMMANDS[*]}"
    exit 1
  fi
}

# Argument 검사
if [ $# -lt 2 ]; then
  print_usage
  exit 1
fi

# 환경 및 브랜치 설정
ENV_NAME=$1
BRANCH=$2

# 필요한 명령어 설정
REQUIRED_COMMANDS="aws jq docker git"

# ECR 설정
AWS_REGION="${AWS_REGION:-}"
ECR_REPOSITORY_NAME="${ECR_REPOSITORY_NAME:-}"
ECR_REGISTRY="${ECR_REGISTRY:-}"
ECR_REPOSITORY_URL="${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}"

# 디렉토리 설정
REPO_DIR=$(pwd)
APP_MODULE=":donation:web-api"

# 필요한 명령어가 실행 가능한지 검사
check_required_commands "$REQUIRED_COMMANDS"

if [ -z "$AWS_REGION" ] || [ -z "$ECR_REGISTRY" ] || [ -z "$ECR_REPOSITORY_NAME" ]; then
  echo "AWS_REGION, ECR_REGISTRY, ECR_REPOSITORY_NAME 환경변수가 필요합니다."
  print_usage
  exit 1
fi

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git repository 내부에서 실행해야 합니다."
  exit 1
fi

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "$BRANCH" ]; then
  echo "현재 checkout 된 브랜치(${CURRENT_BRANCH})와 배포 대상 브랜치(${BRANCH})가 다릅니다."
  exit 1
fi

echo "========================================"
echo "Build & Push to ECR"
echo "========================================"
echo "Branch: ${BRANCH}"
echo "Environment: ${ENV_NAME}"
echo "ECR: ${ECR_REPOSITORY_URL}"
echo "========================================"

# 1. Backend 코드 확인
echo ""
echo "[1/5] Backend 코드 확인..."
cd "$REPO_DIR"

# 브랜치명 + 커밋 해시로 VERSION 확정
COMMIT_HASH=$(git rev-parse --short=8 HEAD)
VERSION="${BRANCH//\//-}-${COMMIT_HASH}"
echo "Commit: ${COMMIT_HASH}"
echo "Final Docker Image Tag: ${VERSION}"

# 2. Gradle 빌드
echo ""
echo "[2/5] Gradle 빌드..."
./gradlew clean "${APP_MODULE}:bootBuildImage" \
  --imageName "${ECR_REPOSITORY_URL}:${VERSION}"

# 3. Docker 이미지 확인
echo ""
echo "[3/5] Docker 이미지 확인..."
docker image inspect "${ECR_REPOSITORY_URL}:${VERSION}" >/dev/null

# 4. AWS 자격 증명 확인
echo ""
echo "[4/5] AWS 자격 증명 확인..."
aws ecr describe-repositories --repository-names "${ECR_REPOSITORY_NAME}" --region "${AWS_REGION}" >/dev/null
aws ecr get-login-password --region "${AWS_REGION}" | docker login --username AWS --password-stdin "${ECR_REGISTRY}"
aws sts get-caller-identity --no-cli-pager

# 5. ECR Push
echo ""
echo "[5/5] ECR Push..."
docker push "${ECR_REPOSITORY_URL}:${VERSION}"

echo ""
echo "========================================"
echo "업로드 완료"
echo "Docker Image Tag: ${VERSION}"
echo "Docker Image URI: ${ECR_REPOSITORY_URL}:${VERSION}"
echo "========================================"
