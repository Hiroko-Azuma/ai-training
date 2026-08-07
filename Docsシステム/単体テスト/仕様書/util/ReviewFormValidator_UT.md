# 単体テスト仕様書: ReviewFormValidator（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.util.ReviewFormValidator`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/util/ReviewFormValidator.java`

## モック対象
なし（`MessageUtil` は静的メッセージ解決のみのため実呼び出し。テスト実行前に `messageSource` を設定する）

---

## validate() - レビュー投稿フォームの入力値検証
**設計書参照: 05個別画面設計_BK11_レビュー投稿入力画面 バリデーション仕様**

| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| RFV-001 | 全項目正常 | - | 全項目正しい値 | errors が空 | `errors.isEmpty()` |
| RFV-002 | レビュアー名未入力 | - | reviewerName=null | reviewerName エラー | 必須メッセージ |
| RFV-003 | レビュアー名空白のみ | - | reviewerName=" " | reviewerName エラー | 同上 |
| RFV-004 | レビュアー名最大長超過 | - | reviewerName=51文字 | reviewerName エラー | 最大長メッセージ |
| RFV-005 | レビュアー名最大長ちょうど | - | reviewerName=50文字 | エラーなし | `errors.containsKey("reviewerName")`=false |
| RFV-006 | 評価未入力 | - | rating=null | rating エラー | 必須メッセージ |
| RFV-007 | 評価が数値でない | - | rating="abc" | rating エラー | 数値形式メッセージ |
| RFV-008 | 評価が範囲未満 | - | rating="0" | rating エラー | 範囲メッセージ |
| RFV-009 | 評価が範囲超過 | - | rating="6" | rating エラー | 範囲メッセージ |
| RFV-010 | 評価が範囲内下限 | - | rating="1" | エラーなし | `errors.containsKey("rating")`=false |
| RFV-011 | 評価が範囲内上限 | - | rating="5" | エラーなし | 同上 |
| RFV-012 | コメント最大長超過 | - | comment=1001文字 | comment エラー | 最大長メッセージ |
| RFV-013 | コメント未入力(任意項目) | - | comment=null | エラーなし | `errors.containsKey("comment")`=false |
