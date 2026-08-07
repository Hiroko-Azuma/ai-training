# 単体テスト仕様書: HelloController（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.HelloController`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/HelloController.java`

## モック対象
なし

---

## hello() - 疎通確認
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| HC-001 | 固定文字列を返す | - | GET /hello | 200 OK | body="Hello, World!" |
