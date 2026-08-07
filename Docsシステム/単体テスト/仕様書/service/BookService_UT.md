# 単体テスト仕様書: BookService（メソッド単位）

## 対象クラス
- クラス名: `jp.co.skig.training.bookshelf.service.BookService`
- 対象ファイル: `src/main/java/jp/co/skig/training/bookshelf/service/BookService.java`

## モック対象
| コンポーネント | クラス | 種別 |
|---|---|---|
| bookMapper | `BookMapper` | `@Mapper`（`@Mock`） |

---

## findAll() - 書籍一覧取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-001 | ページオフセット計算 | page=2, pageSize=20 | page=2, pageSize=20 | offset=40 で mapper 呼び出し | `verify(bookMapper).findAll(..., 20, 40)` |
| BS-002 | 検索条件をそのまま委譲（出版社含む） | - | title, author, categoryId, publisher 等指定 | mapper に同じ値を委譲 | 引数一致 |

## count() - 件数取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-003 | 件数取得成功 | mapper が5を返す | 検索条件（出版社含む） | 5 を返す | 戻り値=5 |

## findDistinctPublishers() - 出版社選択肢取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-016 | 出版社選択肢を重複排除して取得 | mapper が重複排除済みリストを返す | - | mapper の戻り値をそのまま返す | 戻り値のリスト内容・順序一致 |

## findById() - 書籍1件取得
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-004 | 存在する書籍を取得 | mapper が Book を返す | bookId=1 | 対象 Book を返す | 戻り値一致 |
| BS-005 | 存在しない書籍 | mapper が null を返す | bookId=999 | null を返す | 戻り値=null |

## isDuplicateIsbn(String) - ISBN重複チェック（新規登録）
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-006 | 重複あり | mapper が既存Bookを返す | isbn="123" | true | 戻り値=true |
| BS-007 | 重複なし | mapper が null を返す | isbn="999" | false | 戻り値=false |

## isDuplicateIsbn(String, Integer) - ISBN重複チェック（編集時・自身除外）
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-008 | 自身のISBN(除外対象) | mapper が bookId=1 の Book を返す | isbn="123", bookId=1 | false | 戻り値=false |
| BS-009 | 他書籍と重複 | mapper が bookId=2 の Book を返す | isbn="123", bookId=1 | true | 戻り値=true |
| BS-010 | 重複なし | mapper が null を返す | isbn="999", bookId=1 | false | 戻り値=false |

## register() - 書籍登録
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-011 | 登録処理呼び出し | - | Book | mapper.insert が1回呼ばれる | `verify(bookMapper, times(1)).insert(book)` |

## update() - 書籍更新
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-012 | 更新成功 | mapper が1を返す | Book | 1 を返す | 戻り値=1 |
| BS-013 | 楽観ロック失敗 | mapper が0を返す | Book | 0 を返す | 戻り値=0 |

## delete() - 書籍削除
| No | テストケース | 前提条件 | 入力 | 期待結果 | 確認項目 |
|----|------------|---------|------|---------|---------|
| BS-014 | 削除成功 | mapper が1を返す | bookId=1 | 1 を返す | 戻り値=1 |
| BS-015 | 対象なし | mapper が0を返す | bookId=999 | 0 を返す | 戻り値=0 |
