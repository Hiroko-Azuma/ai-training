# 単体テスト仕様書: BookEditController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.BookEditController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/BookEditController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |
| categoryService | `CategoryService` | `@Service`（`@MockitoBean`） |

---

# BK06: 書籍編集入力画面

## editForm() - 編集入力画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK06-001 | セッション復元（同一bookId） | セッションに同一bookIdのフォームあり | bookId=1 | セッション値を表示 | `bookEditForm`＝セッション値 |
| BK06-002 | DBから初期表示 | セッションなし、書籍あり（is_recommended=true） | bookId=1 | DB値から生成したフォーム表示 | `bookEditForm.title` 等が一致、`isRecommended`がtrue |
| BK06-002b | DBから初期表示（お勧めフラグOFF） | 書籍のis_recommended=false | bookId=1 | DB値から生成したフォーム表示 | `isRecommended`がfalse |
| BK06-003 | 書籍が存在しない | 書籍なし | bookId=999 | エラー画面 | `errorMessage`=notfound.book |

## confirmEdit(GET) - 編集確認画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK07-001 | セッションなし | - | bookId=1 | 編集入力へリダイレクト | redirect=`/book/edit/1` |
| BK07-002 | bookId不一致 | セッションに別bookIdのフォーム | bookId=1 | 編集入力へリダイレクト | redirect=`/book/edit/1` |
| BK07-003 | セッションあり・一致 | セッションに同一bookIdのフォーム | bookId=1 | 確認画面表示 | `categoryName` 解決済み |

## confirmEdit(POST) - バリデーションして確認画面へ
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK07-004 | バリデーションエラー | - | title未入力 | 入力画面に戻る | view=`book/BK06_BookEditInput`, `errors` あり |
| BK07-005 | バリデーション成功 | - | 全項目正常 | 確認画面へリダイレクト | redirect=`/book/edit/1/confirm`、セッション保存 |

## update() - 更新実行
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK07-006 | セッションなし | - | bookId=1 | 編集入力へリダイレクト | redirect=`/book/edit/1` |
| BK07-007 | ISBN重複 | isDuplicateIsbn=true | - | 確認画面にエラー | `errorMessage`=validation.duplicate.isbn |
| BK07-008 | 楽観ロック失敗 | update が0を返す | - | エラー画面 | `errorMessage`=concurrent.update, セッション削除 |
| BK07-009 | 更新成功 | update が1を返す、form.isRecommended=true | - | 完了画面へリダイレクト | redirect=`/book/edit/complete?bookId=1`, セッション削除, `Book.isRecommended`がtrueでMapperへ渡る |
| BK07-010 | DBエラー | update が例外送出 | - | 確認画面にエラー | `errorMessage`=db.error.update |

## cancelEdit() - 編集キャンセル
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK07-011 | キャンセル | セッションにフォームあり | bookId=1 | 詳細画面へリダイレクト | セッション削除、redirect=`/book/detail/1` |

---

# BK08: 書籍編集完了画面

## updateComplete() - 完了画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK08-001 | 完了画面表示 | - | bookId=1 | 完了画面表示 | `bookId`=1 |
