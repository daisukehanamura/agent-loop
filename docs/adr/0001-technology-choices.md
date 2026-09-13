# ADR 0001: 技術選定

- ステータス: 採用
- 日付: 2026-09-13

## 背景

普段の業務がレガシー Java・テストなし・CI なしの環境なので、その対極を実際に作って身につけたい。
同時に、転職活動で使える実物のプロダクトにもしたい。

## 決定

| 領域 | 選択 | 理由 |
|---|---|---|
| 言語(BE) | Kotlin 2.3 | null 安全・value class でドメインを型で守れる。Java の知識がそのまま活きる |
| FW(BE) | Spring Boot 4.1 | 業務で使う Spring の最新形を押さえる。Java 21 仮想スレッド対応 |
| JDK | Corretto 21 (LTS) | Spring Boot 4 が対応する LTS で、情報量が最も多い |
| ビルド | Gradle 9 (Kotlin DSL) + Version Catalog | バージョンを 1 箇所に集約できる |
| DB | PostgreSQL 18 | 標準的で、Testcontainers との相性が良い |
| マイグレーション | Flyway | スキーマの変更履歴がファイルとして残る。`ddl-auto` は使わない |
| SPA | Vite 8 + React 19 | Next.js は SSR/RSC の複雑さが要件に見合わない。gzip 80KB に収まる |
| 型 | TypeScript 7 | strict + noUncheckedIndexedAccess で実行時エラーを型で潰す |
| CSS | Tailwind 4 | UI を作り込む前提でクラス名の設計コストを省く |
| Lint/Format | Spotless(ktlint) / Biome | 1 ツールで整形と lint を兼ねる |
| テスト | JUnit5 + MockK + Kotest assertions + Testcontainers / Vitest + Playwright | DB をモックせず実物で検証する |
| アーキ検証 | ArchUnit | 設計ルールを CI で強制する。ドキュメントだけでは守られない |
| AI | Claude (Agent SDK ではなく Claude Code Action + `/loop`) | 自作ハーネスより先に、既製のループを回して運用感を掴む |

## 却下した案

- **Next.js**: 要件に対して重い。SSR も RSC も現時点では不要。
- **detekt**: 安定版が Kotlin 2.3 と非互換、alpha は configuration-cache 非対応。
  Spotless(ktlint) + `allWarningsAsErrors` + ArchUnit で当面代替する。
- **Claude Agent SDK による自作ハーネス**: 学習価値は高いが、まず既製の仕組みを回して
  「どこが足りないか」を体感してから作るほうが順番として正しい。
- **Java 25 / 26**: 新しすぎて周辺ツールの対応が読めない。21 LTS を選ぶ。

## 結果

- 縦の貫通（Postgres → Spring Boot → React → E2E）が動く状態でスタートできた。
- 設計ルールが ArchUnit でテスト化されているので、AI に実装させても構造が壊れない。
