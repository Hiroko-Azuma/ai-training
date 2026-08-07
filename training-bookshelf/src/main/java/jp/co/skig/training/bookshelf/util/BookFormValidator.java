package jp.co.skig.training.bookshelf.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.form.BookRegisterForm;

/**
 * 書籍登録・編集フォームのバリデーションユーティリティ
 */
public final class BookFormValidator {

  private BookFormValidator() {
  }

  /**
   * 書籍登録・編集フォームの入力値を検証する
   * @param form 入力フォーム
   * @return 項目名をキーとしたエラーメッセージのマップ
   */
  public static Map<String, String> validate(BookRegisterForm form) {
    Map<String, String> errors = new LinkedHashMap<>();

    validateTitle(form.getTitle(), errors);
    validateAuthor(form.getAuthor(), errors);
    validatePublisher(form.getPublisher(), errors);
    validatePublishedDate(form.getPublishedDate(), errors);
    validateIsbn(form.getIsbn(), errors);
    validateCategoryId(form.getCategoryId(), errors);
    validatePrice(form.getPrice(), errors);
    validateDescription(form.getDescription(), errors);

    return errors;
  }

  private static void validateTitle(String title, Map<String, String> errors) {
    if (title == null || title.isBlank()) {
      errors.put("title", MessageUtil.getMessage("validation.required", "タイトル"));
    } else if (title.length() > BookConstants.TITLE_MAX_LENGTH) {
      errors.put("title",
          MessageUtil.getMessage("validation.length.max", "タイトル", BookConstants.TITLE_MAX_LENGTH));
    }
  }

  private static void validateAuthor(String author, Map<String, String> errors) {
    if (author == null || author.isBlank()) {
      errors.put("author", MessageUtil.getMessage("validation.required", "著者"));
    } else if (author.length() > BookConstants.AUTHOR_MAX_LENGTH) {
      errors.put("author",
          MessageUtil.getMessage("validation.length.max", "著者", BookConstants.AUTHOR_MAX_LENGTH));
    }
  }

  private static void validatePublisher(String publisher, Map<String, String> errors) {
    if (publisher == null || publisher.isBlank()) {
      errors.put("publisher", MessageUtil.getMessage("validation.required", "出版社"));
    } else if (publisher.length() > BookConstants.PUBLISHER_MAX_LENGTH) {
      errors.put("publisher",
          MessageUtil.getMessage("validation.length.max", "出版社", BookConstants.PUBLISHER_MAX_LENGTH));
    }
  }

  private static void validatePublishedDate(String publishedDate, Map<String, String> errors) {
    if (publishedDate == null || publishedDate.isBlank()) {
      errors.put("publishedDate", MessageUtil.getMessage("validation.required", "出版日"));
      return;
    }
    try {
      LocalDate date = LocalDate.parse(publishedDate, DateTimeFormatter.ISO_LOCAL_DATE);
      if (date.isAfter(LocalDate.now())) {
        errors.put("publishedDate", MessageUtil.getMessage("validation.date.future", "出版日"));
      }
    } catch (DateTimeParseException e) {
      errors.put("publishedDate", MessageUtil.getMessage("validation.date.format", "出版日"));
    }
  }

  private static void validateIsbn(String isbn, Map<String, String> errors) {
    if (isbn == null || isbn.isBlank()) {
      errors.put("isbn", MessageUtil.getMessage("validation.required", "ISBN"));
    } else if (!isbn.matches("\\d{10}|\\d{13}")) {
      errors.put("isbn", MessageUtil.getMessage("validation.isbn.format"));
    }
  }

  private static void validateCategoryId(String categoryId, Map<String, String> errors) {
    if (categoryId == null || categoryId.isBlank()) {
      errors.put("categoryId", MessageUtil.getMessage("validation.required", "カテゴリ"));
    }
  }

  private static void validatePrice(String price, Map<String, String> errors) {
    if (price == null || price.isBlank()) {
      errors.put("price", MessageUtil.getMessage("validation.required", "価格"));
      return;
    }
    try {
      int value = Integer.parseInt(price);
      if (value < 0) {
        errors.put("price", MessageUtil.getMessage("validation.number.min", "価格", 0));
      }
    } catch (NumberFormatException e) {
      errors.put("price", MessageUtil.getMessage("validation.number.format", "価格"));
    }
  }

  private static void validateDescription(String description, Map<String, String> errors) {
    if (description != null && description.length() > BookConstants.DESCRIPTION_MAX_LENGTH) {
      errors.put("description",
          MessageUtil.getMessage("validation.length.max", "概要", BookConstants.DESCRIPTION_MAX_LENGTH));
    }
  }
}
