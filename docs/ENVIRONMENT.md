# 開発環境の注意点

構築時点 (2026-09-13) の実機での確認結果と、残っている課題。

## 必要なもの

| ツール | 必要バージョン | 用途 |
|---|---|---|
| JDK | 21 (LTS) | バックエンド。Gradle の toolchain が 21 を要求する |
| Node.js | 24 LTS 推奨 (22.22.2 以上) | フロントエンド |
| Docker | 起動していること | Testcontainers の結合テストに必須 |
| Make | macOS 標準で入っている | 全コマンドの入口 |

## この環境で分かっていること

### ポート 5432 はローカルの別 Postgres が使っている

compose の Postgres はホスト側 **15432** に出している。`DB_URL` のデフォルトもそれに合わせてある。
5432 を空けたい場合は `compose.yaml` と `application.yaml` の両方を戻すこと。

### JDK が 2 つ入っていて、PATH 上は 17

```
PATH 上           : Microsoft OpenJDK 17.0.9
java_home -v 21 : Amazon Corretto 21.0.1  ← ビルドで使うのはこちら
```

`Makefile` が `JAVA_HOME` を 21 に固定しているので、`make` 経由なら意識しなくてよい。
`./gradlew` を直接叩くときは自分で指定する。

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### 未対応: JDK 21 のパッチが古い

Corretto 21.0.1 は 2023-10 リリースで、以降のセキュリティパッチが当たっていない。
動作に支障はないが、更新しておくのが望ましい。

```bash
brew install --cask temurin@21
```

### 未対応: Node が 22.17.0 で、jsdom の要求を満たしていない

jsdom 30 は `^22.22.2 || ^24.15.0 || >=26` を要求する。
現状テストは全て通っているので実害は出ていないが、警告が出る。CI は Node 24 を使っている。

```bash
brew install node@24
echo 'export PATH="/usr/local/opt/node@24/bin:$PATH"' >> ~/.zshrc
```

`node@24` は keg-only なので、既存の `/usr/local/bin/node` (v22) とは共存できる。
PATH の順序で切り替わるので、戻したくなったら該当行を消すだけでよい。
