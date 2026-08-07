package jp.co.skig.training.bookshelf.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 書籍編集フォーム
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookEditForm extends BookRegisterForm {

  /** 編集対象の書籍ID */
  private Integer bookId;

  /** 楽観的ロック用の更新日時（DBの既存値） */
  private String updatedAt;
}
