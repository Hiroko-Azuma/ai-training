package jp.co.skig.training.bookshelf.form;

import lombok.Data;

/**
 * 書籍登録フォーム
 */
@Data
public class BookRegisterForm {
  private String title;
  private String author;
  private String publisher;
  private String publishedDate;
  private String isbn;
  private String categoryId;
  private String price;
  private String description;
}
