# 単体テスト仕様書: ExceptionLogger（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.util.ExceptionLogger`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/util/ExceptionLogger.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| messageSource | `MessageSource`（`MessageUtil` 経由） | `@Mock` |

---

## log() - 例外ログ出力
**設計書参照: 06メッセージ一覧 log.error.exception**

| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| EL-001 | 例外ログが例外を投げずに完了する | messageSource がメッセージ整形を返す | RuntimeException("test error") | 例外を送出しない | `assertDoesNotThrow` |
