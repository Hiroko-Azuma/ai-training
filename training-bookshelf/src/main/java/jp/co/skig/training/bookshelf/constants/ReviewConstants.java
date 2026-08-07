package jp.co.skig.training.bookshelf.constants;

/**
 * レビュー機能定数クラス
 */
public final class ReviewConstants {

  private ReviewConstants() {
  }

  /** レビュアー名最大文字数 */
  public static final int REVIEWER_NAME_MAX_LENGTH = 50;

  /** コメント最大文字数 */
  public static final int COMMENT_MAX_LENGTH = 1000;

  /** 評価の最小値 */
  public static final int RATING_MIN = 1;

  /** 評価の最大値 */
  public static final int RATING_MAX = 5;

  /** セッションキー：レビュー入力フォーム */
  public static final String SESSION_REVIEW_FORM = "reviewForm";

  /** セッションキー：投稿完了レビュアー名（BK13表示用） */
  public static final String SESSION_COMPLETED_REVIEWER_NAME = "completedReviewerName";

  /** セッションキー：投稿完了評価（BK13表示用） */
  public static final String SESSION_COMPLETED_RATING = "completedRating";
}
