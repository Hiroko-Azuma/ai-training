package jp.co.skig.training.bookshelf.util;

import java.util.LinkedHashMap;
import java.util.Map;
import jp.co.skig.training.bookshelf.constants.ReviewConstants;
import jp.co.skig.training.bookshelf.form.ReviewForm;

/**
 * レビュー投稿フォームのバリデーションユーティリティ
 */
public final class ReviewFormValidator {

  private ReviewFormValidator() {
  }

  /**
   * レビュー投稿フォームの入力値を検証する
   * @param form 入力フォーム
   * @return 項目名をキーとしたエラーメッセージのマップ
   */
  public static Map<String, String> validate(ReviewForm form) {
    Map<String, String> errors = new LinkedHashMap<>();

    if (form.getReviewerName() == null || form.getReviewerName().isBlank()) {
      errors.put("reviewerName", MessageUtil.getMessage("validation.required", "レビュアー名"));
    } else if (form.getReviewerName().length() > ReviewConstants.REVIEWER_NAME_MAX_LENGTH) {
      errors.put("reviewerName", MessageUtil.getMessage("validation.length.max", "レビュアー名",
          ReviewConstants.REVIEWER_NAME_MAX_LENGTH));
    }

    if (form.getRating() == null || form.getRating().isBlank()) {
      errors.put("rating", MessageUtil.getMessage("validation.required", "評価"));
    } else {
      try {
        int rating = Integer.parseInt(form.getRating());
        if (rating < ReviewConstants.RATING_MIN || rating > ReviewConstants.RATING_MAX) {
          errors.put("rating", MessageUtil.getMessage("validation.number.range", "評価",
              ReviewConstants.RATING_MIN, ReviewConstants.RATING_MAX));
        }
      } catch (NumberFormatException e) {
        errors.put("rating", MessageUtil.getMessage("validation.number.format", "評価"));
      }
    }

    if (form.getComment() != null && form.getComment().length() > ReviewConstants.COMMENT_MAX_LENGTH) {
      errors.put("comment",
          MessageUtil.getMessage("validation.length.max", "コメント", ReviewConstants.COMMENT_MAX_LENGTH));
    }

    return errors;
  }
}
