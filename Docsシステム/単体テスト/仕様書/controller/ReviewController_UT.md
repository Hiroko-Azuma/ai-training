# 単体テスト仕様書: ReviewController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.controller.ReviewController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/controller/ReviewController.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookService | `BookService` | `@Service`（`@MockitoBean`） |
| reviewService | `ReviewService` | `@Service`（`@MockitoBean`） |

---

# BK11: レビュー投稿入力画面

## newForm() - 投稿入力画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK11-001 | 書籍なし | 書籍が存在しない | bookId=999 | エラー画面 | `errorMessage`=common.system.error |
| BK11-002 | 初回表示 | 書籍あり、セッションなし | bookId=1 | 空フォーム表示 | `reviewForm` が新規インスタンス |
| BK11-003 | 再表示（セッション復元） | 書籍あり、セッションにフォームあり | bookId=1 | セッション値を表示 | `reviewForm`＝セッション値 |

## cancelReview() - 投稿キャンセル
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK11-004 | キャンセル | セッションにフォームあり | bookId=1 | 詳細画面へリダイレクト | セッション削除、redirect=`/book/detail/1` |

---

# BK12: レビュー投稿確認画面

## confirmReview(GET) - 確認画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK12-001 | セッションなし | - | bookId=1 | 入力画面へリダイレクト | redirect=`/books/1/reviews/new` |
| BK12-002 | 書籍なし | セッションにフォームあり、書籍なし | bookId=999 | エラー画面 | `errorMessage`=common.system.error |
| BK12-003 | セッションあり | セッションにフォームあり、書籍あり | bookId=1 | 確認画面表示 | `reviewForm`＝セッション値 |

## confirmReview(POST) - バリデーションして確認画面へ
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK12-004 | バリデーションエラー | - | rating未入力 | 入力画面に戻る | view=`book/BK11_ReviewPostInput`, `errors` あり |
| BK12-005 | バリデーション成功 | - | 全項目正常 | 確認画面へリダイレクト | redirect=`/books/1/reviews/confirm`, セッション保存 |

## postReview() - レビュー登録実行
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK12-006 | セッションなし | - | bookId=1 | 入力画面へリダイレクト | redirect=`/books/1/reviews/new` |
| BK12-007 | 登録成功 | セッションにフォームあり | bookId=1 | 完了画面へリダイレクト | redirect=`/books/1/reviews/complete`, セッション更新（完了情報保存、フォーム削除） |
| BK12-008 | DBエラー | register が例外送出 | bookId=1 | 確認画面にエラー | `errorMessage`=db.error.insert |

---

# BK13: レビュー投稿完了画面

## reviewComplete() - 完了画面表示
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BK13-001 | 書籍なし | 書籍が存在しない | bookId=999 | エラー画面 | `errorMessage`=common.system.error |
| BK13-002 | 完了画面表示 | 書籍あり、セッションに完了情報あり | bookId=1 | 完了画面表示、セッション削除 | `reviewerName`, `rating` の値一致 |
