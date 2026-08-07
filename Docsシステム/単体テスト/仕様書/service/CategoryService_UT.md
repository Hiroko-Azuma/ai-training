# 単体テスト仕様書: CategoryService（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.service.CategoryService`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/service/CategoryService.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| categoryMapper | `CategoryMapper` | `@Mapper`（`@Mock`） |

---

## findAll() - カテゴリ一覧取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| CS-001 | カテゴリ複数件取得 | mapper が3件返す | なし | 3件のリストを返す | `size()==3`、中身の値一致 |
| CS-002 | カテゴリ0件 | mapper が空リストを返す | なし | 空リストを返す | `isEmpty()` |
