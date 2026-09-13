/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** バックエンド API のベースURL。未設定なら Vite の proxy 経由で /api を使う。 */
  readonly VITE_API_BASE_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
