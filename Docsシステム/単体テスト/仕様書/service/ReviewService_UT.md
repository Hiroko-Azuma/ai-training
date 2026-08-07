# 単体テスト仕様書: ReviewService（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.service.ReviewService`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/service/ReviewService.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| reviewMapper | `ReviewMapper` | `@Mapper`（`@Mock`） |

---

## findByBookId() - 書籍のレビュー一覧取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| RS-001 | レビュー複数件取得 | mapper が2件返す | bookId=1 | 2件のリストを返す | 中身の値一致 |
| RS-002 | レビュー0件 | mapper が空リストを返す | bookId=1 | 空リストを返す | `isEmpty()` |

## register() - レビュー登録
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| RS-003 | 登録処理呼び出し | - | Review | mapper.insert が1回呼ばれる | `verify(reviewMapper, times(1)).insert(review)` |
