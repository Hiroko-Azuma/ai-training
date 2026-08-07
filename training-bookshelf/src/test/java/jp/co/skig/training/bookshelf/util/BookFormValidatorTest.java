package jp.co.skig.training.bookshelf.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jp.co.skig.training.bookshelf.form.BookRegisterForm;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

/**
 * 単体テスト仕様書: BookFormValidator_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class BookFormValidatorTest {

  @Mock
  private MessageSource messageSource;

  @BeforeEach
  void setUp() {
    new MessageUtil(messageSource);
    lenient().when(messageSource.getMessage(org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private BookRegisterForm validForm() {
    BookRegisterForm form = new BookRegisterForm();
    form.setTitle("有効なタイトル");
    form.setAuthor("有効な著者");
    form.setPublisher("有効な出版社");
    form.setPublishedDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    form.setIsbn("1234567890");
    form.setCategoryId("1");
    form.setPrice("1000");
    form.setDescription("概要");
    return form;
  }

  @Test
  void BFV_001_全項目正常() {
    // Given: 全項目が正しい入力フォーム
    BookRegisterForm form = validForm();

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).isEmpty();
  }

  @Test
  void BFV_002_タイトル未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setTitle(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("title");
    assertThat(errors.get("title")).isEqualTo("validation.required");
  }

  @Test
  void BFV_003_タイトル空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setTitle(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("title");
  }

  @Test
  void BFV_004_タイトル最大長超過() {
    // Given: 101文字
    BookRegisterForm form = validForm();
    form.setTitle("あ".repeat(101));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("title");
    assertThat(errors.get("title")).isEqualTo("validation.length.max");
  }

  @Test
  void BFV_005_タイトル最大長ちょうど() {
    // Given: 100文字
    BookRegisterForm form = validForm();
    form.setTitle("あ".repeat(100));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("title");
  }

  @Test
  void BFV_006_著者未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setAuthor(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("author");
  }

  @Test
  void BFV_006b_著者空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setAuthor(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("author");
  }

  @Test
  void BFV_007_著者最大長超過() {
    // Given: 51文字
    BookRegisterForm form = validForm();
    form.setAuthor("あ".repeat(51));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("author");
  }

  @Test
  void BFV_008_出版社未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublisher(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publisher");
  }

  @Test
  void BFV_008b_出版社空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublisher(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publisher");
  }

  @Test
  void BFV_009_出版社最大長超過() {
    // Given: 51文字
    BookRegisterForm form = validForm();
    form.setPublisher("あ".repeat(51));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publisher");
  }

  @Test
  void BFV_010_出版日未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublishedDate(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publishedDate");
    assertThat(errors.get("publishedDate")).isEqualTo("validation.required");
  }

  @Test
  void BFV_010b_出版日空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublishedDate(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publishedDate");
  }

  @Test
  void BFV_011_出版日フォーマット不正() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublishedDate("2024/01/01");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publishedDate");
    assertThat(errors.get("publishedDate")).isEqualTo("validation.date.format");
  }

  @Test
  void BFV_012_出版日が未来日() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublishedDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("publishedDate");
    assertThat(errors.get("publishedDate")).isEqualTo("validation.date.future");
  }

  @Test
  void BFV_013_出版日が本日() {
    // Given
    BookRegisterForm form = validForm();
    form.setPublishedDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("publishedDate");
  }

  @Test
  void BFV_014_ISBN未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setIsbn(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("isbn");
  }

  @Test
  void BFV_014b_ISBN空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setIsbn(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("isbn");
  }

  @Test
  void BFV_015_ISBN桁数不正() {
    // Given
    BookRegisterForm form = validForm();
    form.setIsbn("12345");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("isbn");
    assertThat(errors.get("isbn")).isEqualTo("validation.isbn.format");
  }

  @Test
  void BFV_016_ISBN10桁正常() {
    // Given
    BookRegisterForm form = validForm();
    form.setIsbn("1234567890");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("isbn");
  }

  @Test
  void BFV_017_ISBN13桁正常() {
    // Given
    BookRegisterForm form = validForm();
    form.setIsbn("1234567890123");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("isbn");
  }

  @Test
  void BFV_018_カテゴリ未選択() {
    // Given
    BookRegisterForm form = validForm();
    form.setCategoryId(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("categoryId");
  }

  @Test
  void BFV_018b_カテゴリ空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setCategoryId(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("categoryId");
  }

  @Test
  void BFV_019_価格未入力() {
    // Given
    BookRegisterForm form = validForm();
    form.setPrice(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("price");
  }

  @Test
  void BFV_019b_価格空白のみ() {
    // Given
    BookRegisterForm form = validForm();
    form.setPrice(" ");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("price");
  }

  @Test
  void BFV_020_価格が数値でない() {
    // Given
    BookRegisterForm form = validForm();
    form.setPrice("abc");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("price");
    assertThat(errors.get("price")).isEqualTo("validation.number.format");
  }

  @Test
  void BFV_021_価格が負数() {
    // Given
    BookRegisterForm form = validForm();
    form.setPrice("-1");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("price");
    assertThat(errors.get("price")).isEqualTo("validation.number.min");
  }

  @Test
  void BFV_022_価格が0() {
    // Given
    BookRegisterForm form = validForm();
    form.setPrice("0");

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("price");
  }

  @Test
  void BFV_023_概要最大長超過() {
    // Given: 1001文字
    BookRegisterForm form = validForm();
    form.setDescription("a".repeat(1001));

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("description");
  }

  @Test
  void BFV_024_概要未入力任意項目() {
    // Given
    BookRegisterForm form = validForm();
    form.setDescription(null);

    // When
    Map<String, String> errors = BookFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("description");
  }
}
