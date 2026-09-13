# CareerDeck

エンジニアがスキルと経歴を登録し、ポートフォリオとして公開するサービス。

このリポジトリは **2026 年時点のベストプラクティスな CI/CD・テスト・設計** を実際に組んだうえで、
**AI が開発を自動で回す（ループエンジニアリング）** ことを試す場でもある。

## 技術構成

**バックエンド** Kotlin 2.3 / Spring Boot 4.1 / Java 21 (Corretto) / Gradle 9 (Kotlin DSL) / PostgreSQL 18 / Flyway
**フロントエンド** Vite 8 / React 19 / TypeScript 7 / Tailwind 4 / TanStack Query
**品質** Spotless(ktlint) / Biome / JUnit5 + MockK + Kotest / Vitest + Testing Library / Testcontainers / Playwright / ArchUnit / Kover
**CI/CD** GitHub Actions / CodeQL / Dependabot / actionlint / Conventional Commits
**AI** Claude Code (`/loop`, カスタムコマンド) / Claude Code Action (CI 駆動) / Claude API (プロダクト機能)

選定理由は [docs/adr/0001-technology-choices.md](docs/adr/0001-technology-choices.md) にある。

## はじめかた

必要なもの: JDK 21、Node 22 以上、Docker

```bash
make setup   # 依存取得 + Playwright ブラウザ
make up      # Postgres 起動
make be      # 別ターミナル: バックエンド → http://localhost:8080
make fe      # 別ターミナル: フロントエンド → http://localhost:5173
```

検証は `make check` に集約してある（CI と同じ内容が走る）。
コマンド一覧は `make` で出る。

## アーキテクチャ

バックエンドはヘキサゴナル（ポート&アダプタ）の軽量版で、機能ごとに縦に切る。

```
<feature>/
├── domain/          ドメインモデル。フレームワーク非依存
├── application/     ユースケース + 出力ポート(interface)
└── adapter/
    ├── web/         Controller / DTO
    └── persistence/ JPA エンティティ / Repository 実装
```

この依存方向は ArchUnit (`LayerDependencyTest`) がテストとして強制する。
ドキュメントに書くだけでは守られないので、CI で落とす。

## AI による開発ループ

3 つの層でループを回している。

**1. ローカル（Claude Code）**

```
/next-task          バックログから 1 件選び、テスト → 実装 → 検証 → PR まで回す
/verify             make check が緑になるまで直し切る
/review-fix <PR#>   レビュー指摘を取り込んで直す
/loop /next-task    上記を繰り返し自走させる
```

入力は [docs/BACKLOG.md](docs/BACKLOG.md)。完了条件をテストで検証できる粒度で書いてある。

**2. CI（GitHub Actions × Claude Code Action）**

- Issue や PR で `@claude` とメンションすると、Claude が実装・修正して PR を出す
- PR が開かれると自動でコードレビューが入る

どちらも `make check` を通すまで完了しない設計にしてある。

**3. プロダクト機能（Claude API）**

職務経歴の文章整形で「生成 → 自己評価 → 再生成」を回す（バックログ 3 番、未実装）。

## 規約

開発時のルールは [CLAUDE.md](CLAUDE.md) に集約。人間も AI も同じものを読む。
この環境で実際に踏んだ落とし穴（Spring Boot 4 の starter 名変更、TypeScript 7 の `baseUrl` 廃止など）も
そこに書いてあるので、詰まったらまず読むこと。
