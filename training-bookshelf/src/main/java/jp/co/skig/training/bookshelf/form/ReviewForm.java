package jp.co.skig.training.bookshelf.form;

import lombok.Data;

/**
 * レビュー投稿フォーム
 */
@Data
public class ReviewForm {
  private String reviewerName;
  private String rating;
  private String comment;
}
