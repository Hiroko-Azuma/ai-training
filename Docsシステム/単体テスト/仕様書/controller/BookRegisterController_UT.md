# 単体テスト仕様書: BookRegisterController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.BookRegisterController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/BookRegisterController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |
| categoryService | `CategoryService` | `@Service`（`@MockitoBean`） |

---

# BK03: 書籍登録入力画面

## newForm() - 入力画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK03-001 | 初回表示 | セッションにフォームなし | - | 空フォーム表示 | `bookRegisterForm` が新規インスタンス |
| BK03-002 | 再表示（セッション復元） | セッションにフォームあり | - | セッションの値を表示 | `bookRegisterForm`＝セッション値 |

## confirmRegister(POST) - 入力チェックして確認画面へ
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK03-003 | バリデーションエラー | - | title未入力 | 入力画面に戻る | view=`book/BK03_BookRegisterInput`, `errors` あり |
| BK03-004 | バリデーション成功 | - | 全項目正常 | 確認画面へリダイレクト | redirect=`/book/create/confirm`, セッションにフォーム保存 |

---

# BK04: 書籍登録確認画面

## confirmRegister(GET) - 確認画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK04-001 | セッションにフォームなし | - | - | 入力画面へリダイレクト | redirect=`/book/create` |
| BK04-002 | セッションにフォームあり | セッションにフォームあり | - | 確認画面表示 | `categoryName` 解決済み |

## register() - 登録実行
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK04-003 | セッションフォームなし | - | - | 入力画面へリダイレクト | redirect=`/book/create` |
| BK04-004 | ISBN重複 | isDuplicateIsbn=true | - | 確認画面にエラー表示 | `errorMessage`=validation.duplicate.isbn |
| BK04-005 | 登録成功 | isDuplicateIsbn=false | - | 完了画面へリダイレクト | redirect先に bookId 含む、セッションのフォーム削除、`Book.isRecommended`がfalse（未チェック） |
| BK04-005b | 登録成功（お勧めフラグON） | isDuplicateIsbn=false, form.isRecommended=true | - | 完了画面へリダイレクト | `Book.isRecommended`がtrueで登録される |
| BK04-006 | DBエラー | register が例外送出 | - | 確認画面にエラー表示 | `errorMessage`=db.error.insert |

## cancelRegister() - 登録キャンセル
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK04-007 | キャンセル | セッションにフォームあり | - | 一覧へリダイレクト | セッション削除、redirect=`/book/list` |

---

# BK05: 書籍登録完了画面

## registerComplete() - 完了画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK05-001 | 完了画面表示 | - | bookId=10 | 完了画面表示 | `bookId`=10 |
