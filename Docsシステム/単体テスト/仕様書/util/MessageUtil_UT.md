# 単体テスト仕様書: MessageUtil（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.util.MessageUtil`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/util/MessageUtil.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| messageSource | `MessageSource` | Spring Bean（`@Mock`） |

---

## getMessage() - メッセージ取得
**設計書参照: 06メッセージ一覧**

| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| MU-001 | メッセージ取得成功 | messageSource がメッセージを返す | code="validation.required", args=["タイトル"] | 解決済みメッセージを返す | 戻り値の文字列一致 |
| MU-002 | メッセージ未存在時はコードを返す | messageSource が `NoSuchMessageException` を送出 | code="no.such.code" | コードそのものを返す | 戻り値が code と一致 |
