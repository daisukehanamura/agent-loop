# GitHub 側のセットアップ

リポジトリ作成時に済ませてあるものと、**あなた自身がやる必要があるもの**の一覧。

## 設定済み

- ブランチ保護 (`main`)
  - 必須チェック: `CI 完了` / `Conventional Commits 準拠か`
  - マージ前に main の最新を取り込むこと (strict)
  - linear history 必須、force push と削除は禁止
  - レビューコメントの解決が必須
- マージ方式は squash のみ。マージ後にブランチを自動削除
- Dependabot (Gradle / npm / GitHub Actions、毎週月曜 9:00 JST)
- `claude` ラベル

## 要対応

### 1. ANTHROPIC_API_KEY を登録する

これがないと `@claude` も自動 PR レビューも動かない（今はスキップされる）。

キーは <https://console.anthropic.com/> で取得する。**対話的なターミナルで**実行すること。

```bash
gh secret set ANTHROPIC_API_KEY
# 貼り付けて Enter
```

> **注意: 非対話環境だと空の値が黙って登録される**
>
> TTY のない環境（エディタ統合のシェル、スクリプト、CI など）で `gh secret set` を
> 引数なしに実行すると、空の stdin を読んで**空文字を登録し、エラーも出さない**。
> `gh secret list` には名前が出るので、一見すると成功したように見える。
>
> 非対話環境で設定するなら値を明示的に渡す。
>
> ```bash
> gh secret set ANTHROPIC_API_KEY --body "$(pbpaste)"   # クリップボードから
> gh secret set ANTHROPIC_API_KEY < ~/.anthropic-key    # ファイルから
> ```
>
> 設定できたかは `gh secret list` では分からない。ワークフローを実際に動かし、
> 「ANTHROPIC_API_KEY の有無を確認」ステップの後続がスキップされないことで確認する。

### 2. Claude Code GitHub App をインストールする

Secret だけでは足りず、App がリポジトリにインストールされている必要がある。

<https://github.com/apps/claude> から `daisukehanamura/agent-loop` を選んでインストールする。

Claude Code の CLI からなら次でも設定できる。

```bash
claude
> /install-github-app
```

### 3. 動作確認

Issue を立てて本文に `@claude` と書くか、`claude` ラベルを付ける。
Actions タブに「Claude」ワークフローが現れれば成功。

## 費用について

`@claude` と自動 PR レビューは、実行のたびに Anthropic API の料金がかかる。
使いすぎが不安なら、自動 PR レビュー (`claude-review.yml`) の
`on:` を `pull_request` から `workflow_dispatch` に変えて手動実行だけにするとよい。
