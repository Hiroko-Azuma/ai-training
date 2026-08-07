# 単体テスト仕様書: BookListController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.BookListController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/BookListController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |
| categoryService | `CategoryService` | `@Service`（`@MockitoBean`） |

---

# BK01: 書籍一覧画面

## list() - 一覧表示（検索・ソート・ページング）
**設計書参照: BK01 検索/ソート/ページング仕様**

| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK01-001 | 初期表示・0件 | 書籍0件 | パラメータなし | view: `book/BK01_BookList` | `noDataMessage` 表示、`books` size=0 |
| BK01-002 | 初期表示・データあり | 書籍3件 | パラメータなし | 一覧表示 | `books` size=3、`noDataMessage`/`searchResultMessage` なし |
| BK01-003 | 検索条件あり・結果あり | 書籍1件 | searchTitle="Java" | 検索結果メッセージ表示 | `searchResultMessage` に件数含む |
| BK01-004 | ページ情報算出・端数あり | 該当41件 | page=1 | 3ページに分割 | `totalPages`=3, `totalCount`=41 |
| BK01-005 | ページ番号不正(0以下)補正 | - | page=0 | currentPage=1に補正 | `currentPage`=1 |
| BK01-006 | セッションの検索条件を利用 | セッションに検索条件あり | パラメータなし | セッション値を使用 | `searchTitle`＝セッション値 |
| BK01-007 | リクエスト値でセッション更新 | セッションに旧検索条件あり | searchTitle="新" | セッションが新値に更新される | セッション属性値の確認 |
| BK01-008 | DBエラー発生 | bookService.count が例外送出 | パラメータなし | エラーメッセージ表示 | `errorMessage`=db.error.select |

## clearSearch() - 検索条件クリア
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK01-009 | 検索条件クリア | セッションに検索条件あり | - | 一覧へリダイレクト | セッション属性が削除される、redirect先=`/book/list` |
