# 単体テスト仕様書: BookDetailController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.BookDetailController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/BookDetailController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |
| reviewService | `ReviewService` | `@Service`（`@MockitoBean`） |

---

# BK02: 書籍詳細画面

## detail() - 書籍詳細とレビュー一覧表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK02-001 | 詳細表示・レビューあり | 書籍あり、レビュー2件(5,4) | bookId=1 | 平均評価4.5表示 | `avgRating`=4.5, `reviews` size=2 |
| BK02-002 | 詳細表示・レビューなし | 書籍あり、レビュー0件 | bookId=1 | 平均評価0 | `avgRating`=0.0 |
| BK02-003 | 書籍が存在しない | bookService が null を返す | bookId=999 | エラー画面 | view=`book/error`, `errorMessage`=notfound.book |
| BK02-004 | DBエラー発生 | bookService が例外送出 | bookId=1 | エラー画面 | `errorMessage`=common.system.error |
| BK02-005 | 検索条件をセッションから引継ぎ | セッションに検索条件あり | bookId=1 | 戻り検索条件が表示される | `backSearchTitle` 等の値一致 |
