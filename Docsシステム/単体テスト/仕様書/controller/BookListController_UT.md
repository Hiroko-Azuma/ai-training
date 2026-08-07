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
| BK01-007b | カテゴリIDパラメータ直接指定 | - | searchCategoryId=2 | 検索条件として反映 | `searchCategoryId`=2 |
| BK01-007c | ソート列・ソート順を直接指定 | - | sortColumn=title, sortOrder=ASC | 検索条件として反映 | `sortColumn`/`sortOrder` |
| BK01-007d | 著者のみで検索結果あり | 該当1件 | searchAuthor="著者" | 検索結果メッセージ表示 | `searchResultMessage` |
| BK01-007e | カテゴリIDのみで検索結果あり | 該当1件 | searchCategoryId=3 | 検索結果メッセージ表示 | `searchResultMessage` |
| BK01-007f | 出版社のみで検索結果あり（完全一致） | 該当1件 | searchPublisher="岩波書店" | 検索結果メッセージ表示 | `searchPublisher`、`searchResultMessage` |
| BK01-007g | 出版社選択肢がモデルに反映される | booksから重複排除した出版社3件 | パラメータなし | 出版社プルダウン用データ取得 | `publishers` に重複排除済みリスト |
| BK01-007h | セッションの出版社検索条件を利用 | セッションに出版社検索条件あり | パラメータなし | セッション値を使用 | `searchPublisher`＝セッション値 |
| BK01-007i | 検索条件が全て空文字は検索結果メッセージなし | - | searchTitle="", searchAuthor="", searchPublisher="" | 未入力扱い | `searchResultMessage` 非表示 |
| BK01-007j | タイトル空文字・出版社指定で検索結果あり | 該当2件 | searchTitle="", searchPublisher="岩波書店" | 検索結果メッセージ表示 | `searchResultMessage` |
| BK01-007k | 著者空文字・カテゴリ指定で検索結果あり | 該当1件 | searchAuthor="", searchCategoryId=4 | 検索結果メッセージ表示 | `searchResultMessage` |
| BK01-008 | DBエラー発生 | bookService.count が例外送出 | パラメータなし | エラーメッセージ表示 | `errorMessage`=db.error.select |

## clearSearch() - 検索条件クリア
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK01-009 | 検索条件クリア | セッションに検索条件（タイトル・著者・カテゴリ・出版社）あり | - | 一覧へリダイレクト | セッション属性（出版社含む）が削除される、redirect先=`/book/list` |

