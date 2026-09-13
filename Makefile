# CareerDeck 開発用タスク。`make` だけで使い方が出る。
SHELL := /bin/bash
JAVA_HOME := $(shell /usr/libexec/java_home -v 21 2>/dev/null)
export JAVA_HOME

.DEFAULT_GOAL := help
.PHONY: help setup up down reset-db be fe test test-be test-fe e2e lint fix check ci-local clean

help: ## このヘルプを表示
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
	  | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-14s\033[0m %s\n", $$1, $$2}'

setup: ## 初回セットアップ (依存取得 + Playwright ブラウザ)
	cd frontend && npm ci && npx playwright install --with-deps chromium
	cd backend && ./gradlew --quiet dependencies > /dev/null

up: ## Postgres を起動
	docker compose up -d --wait

down: ## Postgres を停止
	docker compose down

reset-db: ## DB を初期化して作り直す
	docker compose down -v && docker compose up -d --wait

be: up ## バックエンドを起動 (http://localhost:8080)
	cd backend && ./gradlew bootRun --args='--spring.profiles.active=local'

fe: ## フロントエンドを起動 (http://localhost:5173)
	cd frontend && npm run dev

test: test-be test-fe ## 全テスト

test-be: ## バックエンドのテスト (Testcontainers を使うので Docker が必要)
	cd backend && ./gradlew test

test-fe: ## フロントエンドのテスト
	cd frontend && npm run test

e2e: ## Playwright E2E (backend と frontend が起動している前提)
	cd frontend && npm run e2e

lint: ## 静的チェック
	cd backend && ./gradlew spotlessCheck
	cd frontend && npm run lint

fix: ## 自動整形
	cd backend && ./gradlew spotlessApply
	cd frontend && npm run lint:fix

check: ## CI と同じ検証を一括実行
	cd backend && ./gradlew checkAll
	cd frontend && npm run checkAll

clean: ## ビルド成果物を削除
	cd backend && ./gradlew clean
	rm -rf frontend/dist frontend/coverage frontend/playwright-report frontend/test-results
