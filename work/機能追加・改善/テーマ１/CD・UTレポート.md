# CD・UTレポート

## テーマ

書籍に「お勧めフラグ」を追加する機能改善

## 対応方針

- CDは `prompt/01.CD/CD_Executor.md`（コーデイング実行プロンプト）・`prompt/01.CD/CD_ルール.md` に従い実施。
- UTは `prompt/02.UT1/単体テスト_Executor.md` に従い実施。
- 入力（正）は [work/機能追加・改善/テーマ１/設計書修正レポート.md](設計書修正レポート.md) に記載の設計書修正内容。
- 対象は `training-bookshelf/src` 配下のソースコードおよび `Docsシステム/単体テスト` 配下の仕様書・テスト結果。

---

## 1. CD（コーディング実行）

設計書修正レポートに記載された `is_recommended`（お勧めフラグ）列・画面項目・SQL文の変更を、実装に反映した。

### 1.1 変更ファイル一覧

| 種別 | ファイル | 変更内容 |
|------|---------|---------|
| DB定義 | `src/main/resources/schema.sql` | `books` テーブルに `is_recommended BOOLEAN NOT NULL DEFAULT FALSE` を追加 |
| 初期データ | `src/main/resources/data.sql` | デモ表示用に `book_id=1,7`（リーダブルコード／達人プログラマー）を `is_recommended=TRUE` に更新するUPDATE文を追加 |
| Entity | `entity/Book.java` | `Boolean isRecommended` フィールドを追加（`description` の後） |
| Form | `form/BookRegisterForm.java` | `Boolean isRecommended` フィールドを追加（`BookEditForm` は継承のため自動反映） |
| Mapper | `mapper/BookMapper.java` | `findById` / `findByIsbn` のSELECT、`insert` / `update` に `is_recommended` 列を追加 |
| Mapper XML | `mapper/BookMapper.xml` | `findAll` のSELECT・GROUP BYに `b.is_recommended` を追加 |
| Controller | `controller/BookRegisterController.java` | `toBook()` で `form.getIsRecommended()` → `Book.isRecommended` をマッピング |
| Controller | `controller/BookEditController.java` | `toForm()` でDB値→フォーム、`toBook()` でフォーム→DB値をマッピング |
| Template | `templates/book/BK01_BookList.html` | タイトル横に「おすすめ」バッジ（`th:if="${book.isRecommended}"`）を追加 |
| Template | `templates/book/BK03_BookRegisterInput.html` | 「お勧めフラグ」チェックボックスを追加 |
| Template | `templates/book/BK04_BookRegisterConfirm.html` | 確認欄に「お勧めフラグ：する／しない」を追加 |
| Template | `templates/book/BK06_BookEditInput.html` | 「お勧めフラグ」チェックボックスを追加（既存値を初期表示） |
| Template | `templates/book/BK07_BookEditConfirm.html` | 確認欄に「お勧めフラグ：する／しない」を追加 |
| CSS | `static/css/style.css` | `.badge-recommended` スタイルを追加（設計書HTMLモックの配色に合わせた赤バッジ） |

`BookFormValidator`・`BookListController`・`BookDetailController` 等は、お勧めフラグに関するバリデーション要件や分岐ロジックが設計書上ないため変更していない（表示上は素通しの項目のため）。

### 1.2 主な差分（Before/After）

#### `entity/Book.java`
```diff
   private Integer categoryId;
   private Integer price;
   private String description;
+  private Boolean isRecommended;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
```

#### `form/BookRegisterForm.java`
```diff
   private String categoryId;
   private String price;
   private String description;
+  private Boolean isRecommended;
 }
```

#### `schema.sql`
```diff
     price INT NOT NULL,
     description TEXT,
+    is_recommended BOOLEAN NOT NULL DEFAULT FALSE,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
```
設計書修正レポートの「04TBL定義.md」記載どおり、位置は `description` の後・`created_at` の前。

#### `mapper/BookMapper.java`（登録INSERT・更新UPDATE）
```diff
   @Insert("""
       INSERT INTO books (title, author, publisher, published_date, isbn,
-                         category_id, price, description, created_at, updated_at)
+                         category_id, price, description, is_recommended, created_at, updated_at)
       VALUES (#{title}, #{author}, #{publisher}, #{publishedDate}, #{isbn},
-              #{categoryId}, #{price}, #{description}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
+              #{categoryId}, #{price}, #{description}, #{isRecommended}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
       """)

   @Update("""
       UPDATE books SET title = #{title}, author = #{author}, publisher = #{publisher},
              published_date = #{publishedDate}, isbn = #{isbn}, category_id = #{categoryId},
-             price = #{price}, description = #{description}, updated_at = CURRENT_TIMESTAMP
+             price = #{price}, description = #{description}, is_recommended = #{isRecommended},
+             updated_at = CURRENT_TIMESTAMP
       WHERE book_id = #{bookId} AND updated_at = #{updatedAt}
       """)
```
設計書修正レポートの「BK04_書籍登録確認画面.md」「BK07_書籍編集確認画面.md」に記載のINSERT/UPDATE文の変更と一致。

#### `controller/BookRegisterController.java` / `BookEditController.java`
```diff
     book.setDescription(form.getDescription());
+    book.setIsRecommended(Boolean.TRUE.equals(form.getIsRecommended()));
     return book;
```
```diff
     form.setDescription(book.getDescription());
+    form.setIsRecommended(book.getIsRecommended());
     form.setUpdatedAt(book.getUpdatedAt().toString());
```
チェックボックス未チェック時は `null` が送信されうるため、`Boolean.TRUE.equals(...)` でNPEなく `false` 扱いにしている。

#### `templates/book/BK01_BookList.html`（一覧のおすすめバッジ）
```diff
             <td>
               <a class="title-link" th:href="@{/book/detail/{id}(id=${book.bookId})}" th:text="${book.title}"></a>
+              <span class="badge-recommended" th:if="${book.isRecommended}">おすすめ</span>
             </td>
```
設計書「BK01_書籍一覧画面.md」の「タイトル横に『おすすめ』バッジを表示」に対応。

#### `templates/book/BK03_BookRegisterInput.html` / `BK06_BookEditInput.html`（チェックボックス）
```diff
+        <div class="form-group">
+          <label for="isRecommended">
+            <input type="checkbox" id="isRecommended" th:field="*{isRecommended}">
+            お勧めフラグ（おすすめ書籍にする）
+          </label>
+        </div>
```

#### `templates/book/BK04_BookRegisterConfirm.html` / `BK07_BookEditConfirm.html`（確認表示）
```diff
         <dt>概要</dt>
         <dd th:text="${bookRegisterForm.description}"></dd>
+        <dt>お勧めフラグ</dt>
+        <dd th:text="${bookRegisterForm.isRecommended} ? 'する' : 'しない'"></dd>
       </dl>
```

### 1.3 ビルド確認

```
./mvnw -q -DskipTests package
```
→ BUILD SUCCESS（コンパイルエラーなし）

---

## 2. UT（単体テスト）

### 2.1 対象クラスの選定

| クラス | 対応 | 理由 |
|--------|------|------|
| `BookRegisterController` | テストケース追加 | `isRecommended` のフォーム→エンティティ変換ロジックが増えたため |
| `BookEditController` | テストケース追加 | `isRecommended` のDB→フォーム／フォーム→エンティティの双方向変換ロジックが増えたため |
| `Book`（Entity） | 対象外 | `@Data` のみでロジックなし |
| `BookRegisterForm`／`BookEditForm`（Form） | 対象外 | フィールドのみでロジックなし |
| `BookMapper`／`BookMapper.xml` | 対象外 | Mapperは結合テスト範囲 |
| `BookListController` | 変更なし | Controller自身のロジックは変更なし（`isRecommended` はモデルを素通りするのみ）。既存13件のテストで一覧表示の分岐は網羅済み |

### 2.2 仕様書の更新

- [BookRegisterController_UT.md](../../../Docsシステム/単体テスト/仕様書/controller/BookRegisterController_UT.md)
  - `BK04-005`（登録成功）に「`Book.isRecommended`がfalse（未チェック）」の確認項目を追記
  - `BK04-005b`（登録成功・お勧めフラグON）を新規追加
- [BookEditController_UT.md](../../../Docsシステム/単体テスト/仕様書/controller/BookEditController_UT.md)
  - `BK06-002`（DBから初期表示）に「書籍のis_recommended=true→フォームのisRecommended=true」の確認項目を追記
  - `BK06-002b`（お勧めフラグOFFの書籍を編集）を新規追加
  - `BK07-009`（更新成功）に「`Book.isRecommended`がtrueでMapperへ渡る」の確認項目を追記

### 2.3 テストコードの更新

- [BookRegisterControllerTest.java](../../../training-bookshelf/src/test/java/jp/co/skig/training/bookshelf/controller/BookRegisterControllerTest.java)
  - `BK04_005_登録成功`: `ArgumentCaptor<Book>` で捕捉した登録エンティティに対し `getIsRecommended()` が `false` であることを検証する行を追加
  - `BK04_005b_登録成功_おすすめフラグON`（新規）: フォームで `isRecommended=true` を指定し、`Book.isRecommended` が `true` でサービスに渡ることを検証
- [BookEditControllerTest.java](../../../training-bookshelf/src/test/java/jp/co/skig/training/bookshelf/controller/BookEditControllerTest.java)
  - 共通ヘルパー `book(int id)` に `isRecommended=true` を設定
  - `BK06_002_DBから初期表示`: `form.getIsRecommended()` が `true` であることの検証を追加
  - `BK06_002b_DBから初期表示_お勧めフラグOFF`（新規）: `isRecommended=false` の書籍を編集した際にフォームへ正しく反映されることを検証
  - `BK07_009_更新成功`: フォームで `isRecommended=true` を設定し、`ArgumentCaptor<Book>` で更新エンティティの `isRecommended` が `true` であることを検証する行を追加

### 2.4 テスト実行結果

```
./mvnw test
```

| 項目 | Before（設計書修正前の既存状態） | After（本対応後） |
|------|------|------|
| 総テスト件数 | 137件 | **139件**（+2） |
| 結果 | ALL GREEN | ALL GREEN |

クラス別（変更ありのみ抜粋）:

| クラス | テスト件数 | 命令 | 分岐 | 行 | メソッド |
|--------|-----------|------|------|----|---------|
| `BookRegisterControllerTest` | 13件 → **14件** | 238→245 (100%) | 12 (100%) | 58→59 (100%) | 9 (100%) |
| `BookEditControllerTest` | 18件 → **19件** | 375→386 (100%) | 22 (100%) | 87→89 (100%) | 10 (100%) |

プロジェクト全体カバレッジ（Before → After）:

| 指標 | Before | After | 目標 |
|------|--------|-------|------|
| 命令(Instruction) | 1850 (100%) | **1868 (100%)** | 95%以上 ✅ |
| 分岐(Branch) | 142/144 (98.6%) | **142/144 (98.6%)**（変更なし・お勧めフラグ処理に分岐なし） | 90%以上 ✅ |
| 行(Line) | 397 (100%) | **400 (100%)** | 95%以上 ✅ |
| メソッド(Method) | 60 (100%) | **60 (100%)**（新規メソッドなし） | 100% ✅ |

未到達分岐2件（`BookListController#list()` の検索結果メッセージ判定のOR分岐）は本対応前から存在するもので、お勧めフラグとは無関係。従来どおり実装維持（理由は [BookListController_Report.md](../../../Docsシステム/単体テスト/テスト結果/BookListController_Report.md) 参照）。

### 2.5 UT時に修正した内容 / 設計書との乖離

- プロダクトコードの不具合修正、設計書との乖離は無し。実装は設計書修正レポートの内容と一致していることを確認済み。

### 2.6 更新した成果物

- [BookRegisterController_Report.md](../../../Docsシステム/単体テスト/テスト結果/BookRegisterController_Report.md)：テスト件数13→14、カバレッジ数値を更新、備考に今回追加したテスト観点を追記
- [BookEditController_Report.md](../../../Docsシステム/単体テスト/テスト結果/BookEditController_Report.md)：テスト件数18→19、カバレッジ数値を更新、備考に今回追加したテスト観点を追記
- [00_テスト実施サマリ.md](../../../Docsシステム/単体テスト/テスト結果/00_テスト実施サマリ.md)：合計テスト件数137→139、プロジェクト全体カバレッジを更新、本レポートへのリンクを追記
- `Docsシステム/単体テスト/テスト結果/jacoco/`：最新の実行結果でJaCoCo HTMLレポート一式を再生成・上書き

---

## 3. 対応範囲外・補足

- 本対応は `training-bookshelf` のCD（実装）とUT（単体テスト）に限定している。`Docsシステム/結合テスト` 配下のケース・データ・結果は未対応（設計書修正レポートの「対応方針」に明記の通り、結合テストの見直しは別対応）。
- `data.sql` はアプリ起動時の初期データのため、動作確認用に既存書籍2件（リーダブルコード、達人プログラマー）を「おすすめ」表示のデモ対象とした。これはテストデータの範囲内の変更であり、テストケースの前提条件には影響しない（コントローラーテストはMockベースのため実データに依存しない）。
