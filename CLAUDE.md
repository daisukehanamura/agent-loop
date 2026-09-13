# CareerDeck

エンジニアがスキル・経歴を登録し、ポートフォリオとして公開するサービス。
学習目的として **2026 年時点のベストプラクティスな CI/CD・テスト・設計** を実践し、
さらに **AI が自動で開発を回す（ループエンジニアリング）** 仕掛けを持たせることを目指す。

## 構成

| ディレクトリ | 中身 |
|---|---|
| `backend/` | Kotlin 2.3 + Spring Boot 4.1 + Gradle (Kotlin DSL) |
| `frontend/` | Vite 8 + React 19 + TypeScript 7 + Tailwind 4 |
| `.github/` | CI/CD, Claude 自動化, Dependabot |
| `docs/` | 設計判断の記録 (ADR) |

## 開発コマンド

`Makefile` が唯一の入口。個別コマンドを覚える必要はない。

```
make setup     # 初回セットアップ
make up        # Postgres 起動 (ホスト側ポートは 15432)
make be        # バックエンド起動 (http://localhost:8080)
make fe        # フロントエンド起動 (http://localhost:5173)
make check     # CI と同じ検証を一括実行  ← コミット前に必ず通す
make fix       # 自動整形
make e2e       # Playwright E2E (be と fe が起動している前提)
```

## 必ず守ること

1. **文章はすべて日本語で書く。** Issue、PR、コミットメッセージ、ドキュメント、
   コード内コメントのいずれも日本語。英語にするのは識別子（クラス名・変数名・
   ブランチ名）と Conventional Commits の type/scope だけ。
   テストメソッド名はバッククォート記法で日本語にする。
2. **`make check` が緑になるまで完了報告しない。** 落ちたまま「たぶん大丈夫」は禁止。
3. **テストを先に書く。** 振る舞いを検証する。実装の写経になっているテストは価値がない。
4. **スキーマ変更は Flyway のみ。** `ddl-auto` は `validate` 固定。既存マイグレーションは編集せず、常に新しい `V{n}__*.sql` を足す。
5. **秘密情報をコミットしない。** `ANTHROPIC_API_KEY` などは `.env`（gitignore 済み）か GitHub Secrets に置く。
6. **カバレッジ閾値を下げて通さない。** 通らないならテストを足す。

## バックエンドの設計ルール

ヘキサゴナル（ポート&アダプタ）の軽量版。ArchUnit (`LayerDependencyTest`) が CI で強制するので、
違反すると必ずテストが落ちる。

```
dev.hanamaru.careerdeck
├── common/              横断関心 (設定・例外ハンドラ)
└── <feature>/           機能ごとに縦に切る
    ├── domain/          ドメインモデル。フレームワーク非依存（Spring も JPA も import 禁止）
    ├── application/     ユースケース。port/ に出力ポートの interface を置く
    └── adapter/
        ├── web/         Controller と DTO
        └── persistence/ JPA エンティティと Repository 実装
```

- ドメインの値は `@JvmInline value class` で包む（`String` の取り違えを型で防ぐ）。
- エンティティ ↔ ドメインの変換は `adapter/persistence` の責務。ドメインに JPA を漏らさない。
- 時刻は必ず `Clock` Bean 経由。`Instant.now()` を直接呼ばない（テストで固定できなくなる）。
- エラーレスポンスは RFC 9457 の `ProblemDetail` に統一する。
- `@Transactional(readOnly = true)` をクラスに、更新メソッドにだけ `@Transactional` を付ける。

## テスト方針

| 層 | やり方 |
|---|---|
| ドメイン | 純粋な単体テスト。モック不要 |
| アプリケーション | MockK で出力ポートだけ差し替える |
| Web/永続化 | `@SpringBootTest` + Testcontainers の実 Postgres。**DB はモックしない** |
| アーキテクチャ | ArchUnit |
| フロント | Vitest + Testing Library。`fetch` を spy する |
| E2E | Playwright。ユーザーの操作をなぞる |

- テスト名は日本語のバッククォート記法で「何がどうなるか」を書く。
- 結合テストで実 DB を使うので、テスト実行には **Docker が起動している必要がある**。

## この環境固有の落とし穴（実際に踏んだもの）

調べ直す前にここを読むこと。

- **Spring Boot 4 で starter 名が変わった**: `spring-boot-starter-web` → `-webmvc`。
  テスト用 starter も `spring-boot-starter-<name>-test` という個別依存になった。
- **`@AutoConfigureMockMvc` の import が移動した**:
  `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`。
  旧 `org.springframework.boot.test.autoconfigure.web.servlet` は存在しない。
- **Jackson が 3 系**: `com.fasterxml.jackson` ではなく `tools.jackson`。
- **detekt は未導入**: 1.23.x は Kotlin 2.0.21 ビルドで Kotlin 2.3 と非互換、2.0.0-alpha は
  configuration-cache 非対応。静的解析は Spotless(ktlint) + `allWarningsAsErrors` + ArchUnit で代替。
  detekt 2.0 stable が出たら導入する。
- **TypeScript 7 で `baseUrl` が廃止**: `paths` だけで書く。
- **`erasableSyntaxOnly` が有効**: コンストラクタのパラメータプロパティ (`constructor(readonly x: T)`)
  は使えない。フィールドを明示的に宣言する。
- **Vitest 5 は `vite.config.ts` の `test` フィールドを受け付けない**: `vitest.config.ts` に分離済み。
- **Postgres 18 のボリューム**: `/var/lib/postgresql/data` ではなく `/var/lib/postgresql` をマウントする。
- **compose の Postgres はホスト 15432**: ローカルに別の Postgres が 5432 で動いているため。
- **ktlint が日本語のプロパティ名を弾く**: ArchUnit のように意図的に使う場合は
  `@Suppress("ktlint:standard:property-naming")` を付ける（KDoc の直後に行コメントを置かないこと）。

## コミットと PR

- Conventional Commits。`feat(backend): ...` / `fix(frontend): ...` / `chore(deps): ...`
  scope は `backend` `frontend` `ci` `docs` `deps` `ai` のいずれか。
- 件名は小文字始まり。CI (`pr-title.yml`) が検証する。
- `main` への直 push はしない。必ず PR を作る。
