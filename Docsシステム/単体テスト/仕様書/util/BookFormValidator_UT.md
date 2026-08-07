# 単体テスト仕様書: BookFormValidator（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.util.BookFormValidator`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/util/BookFormValidator.java`

## モック対象
なし（`MessageUtil` は静的メッセージ解決のみのため実呼び出し。テスト実行前に `messageSource` を設定する）

---

## validate() - 書籍登録・編集フォームの入力値検証
**設計書参照: 05個別画面設計_BK03_書籍登録入力画面 バリデーション仕様**

| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BFV-001 | 全項目正常 | - | 全項目正しい値 | errors が空 | `errors.isEmpty()` |
| BFV-002 | タイトル未入力 | - | title=null | title エラー | `errors.get("title")`=必須メッセージ |
| BFV-003 | タイトル空白のみ | - | title=" " | title エラー | 同上 |
| BFV-004 | タイトル最大長超過 | - | title=101文字 | title エラー | 最大長メッセージ |
| BFV-005 | タイトル最大長ちょうど | - | title=100文字 | title エラーなし | `errors.containsKey("title")`=false |
| BFV-006 | 著者未入力 | - | author=null | author エラー | 必須メッセージ |
| BFV-007 | 著者最大長超過 | - | author=51文字 | author エラー | 最大長メッセージ |
| BFV-008 | 出版社未入力 | - | publisher=null | publisher エラー | 必須メッセージ |
| BFV-009 | 出版社最大長超過 | - | publisher=51文字 | publisher エラー | 最大長メッセージ |
| BFV-010 | 出版日未入力 | - | publishedDate=null | publishedDate エラー | 必須メッセージ |
| BFV-011 | 出版日フォーマット不正 | - | publishedDate="2024/01/01" | publishedDate エラー | 日付形式メッセージ |
| BFV-012 | 出版日が未来日 | - | publishedDate=明日の日付 | publishedDate エラー | 未来日メッセージ |
| BFV-013 | 出版日が本日 | - | publishedDate=本日の日付 | エラーなし | `errors.containsKey("publishedDate")`=false |
| BFV-014 | ISBN未入力 | - | isbn=null | isbn エラー | 必須メッセージ |
| BFV-015 | ISBN桁数不正 | - | isbn="12345" | isbn エラー | ISBN形式メッセージ |
| BFV-016 | ISBN10桁正常 | - | isbn="1234567890" | エラーなし | `errors.containsKey("isbn")`=false |
| BFV-017 | ISBN13桁正常 | - | isbn="1234567890123" | エラーなし | 同上 |
| BFV-018 | カテゴリ未選択 | - | categoryId=null | categoryId エラー | 必須メッセージ |
| BFV-019 | 価格未入力 | - | price=null | price エラー | 必須メッセージ |
| BFV-020 | 価格が数値でない | - | price="abc" | price エラー | 数値形式メッセージ |
| BFV-021 | 価格が負数 | - | price="-1" | price エラー | 最小値メッセージ |
| BFV-022 | 価格が0 | - | price="0" | エラーなし | `errors.containsKey("price")`=false |
| BFV-023 | 概要最大長超過 | - | description=1001文字 | description エラー | 最大長メッセージ |
| BFV-024 | 概要未入力(任意項目) | - | description=null | エラーなし | `errors.containsKey("description")`=false |
