package jp.co.skig.training.bookshelf.constants;

/**
 * 書籍機能定数クラス
 */
public final class BookConstants {

  private BookConstants() {
  }

  /** 1ページあたりの表示件数 */
  public static final int PAGE_SIZE = 20;

  /** デフォルトソート列 */
  public static final String DEFAULT_SORT_COLUMN = "bookId";

  /** デフォルトソート順 */
  public static final String DEFAULT_SORT_ORDER = "DESC";

  /** タイトル最大文字数 */
  public static final int TITLE_MAX_LENGTH = 100;

  /** 著者最大文字数 */
  public static final int AUTHOR_MAX_LENGTH = 50;

  /** 出版社最大文字数 */
  public static final int PUBLISHER_MAX_LENGTH = 50;

  /** 概要最大文字数 */
  public static final int DESCRIPTION_MAX_LENGTH = 1000;

  /** セッションキー：検索条件（タイトル） */
  public static final String SESSION_SEARCH_TITLE = "searchTitle";

  /** セッションキー：検索条件（著者） */
  public static final String SESSION_SEARCH_AUTHOR = "searchAuthor";

  /** セッションキー：検索条件（カテゴリID） */
  public static final String SESSION_SEARCH_CATEGORY_ID = "searchCategoryId";

  /** セッションキー：検索条件（出版社） */
  public static final String SESSION_SEARCH_PUBLISHER = "searchPublisher";

  /** セッションキー：登録入力フォーム */
  public static final String SESSION_REGISTER_FORM = "bookRegisterForm";

  /** セッションキー：編集入力フォーム */
  public static final String SESSION_EDIT_FORM = "bookEditForm";
}
