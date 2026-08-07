# 単体テスト仕様書: BookDeleteController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.BookDeleteController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/BookDeleteController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |

---

# BK09: 書籍削除確認画面

## confirmDelete() - 削除確認画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK09-001 | 書籍あり | 書籍が存在 | bookId=1 | 削除確認画面表示 | `book` が対象と一致 |
| BK09-002 | 書籍なし | 書籍が存在しない | bookId=999 | エラー画面 | `errorMessage`=notfound.book |

## delete() - 削除実行
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK09-003 | 削除成功 | delete が1を返す | bookId=1 | 完了画面へリダイレクト | redirect=`/book/delete/complete` |
| BK09-004 | 対象なし(0件削除) | delete が0を返す | bookId=999 | エラー画面 | `errorMessage`=notfound.book |
| BK09-005 | DBエラー | delete が例外送出 | bookId=1 | 削除確認画面にエラー | `errorMessage`=db.error.delete |

---

# BK10: 書籍削除完了画面

## deleteComplete() - 完了画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK10-001 | 完了画面表示 | - | - | view=`book/BK10_BookDeleteComplete` | ステータス200 |
