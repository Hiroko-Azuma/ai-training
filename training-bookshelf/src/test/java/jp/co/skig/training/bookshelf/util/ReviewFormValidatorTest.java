package jp.co.skig.training.bookshelf.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.util.Map;
import jp.co.skig.training.bookshelf.form.ReviewForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

/**
 * 単体テスト仕様書: ReviewFormValidator_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class ReviewFormValidatorTest {

  @Mock
  private MessageSource messageSource;

  @BeforeEach
  void setUp() {
    new MessageUtil(messageSource);
    lenient().when(messageSource.getMessage(org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private ReviewForm validForm() {
    ReviewForm form = new ReviewForm();
    form.setReviewerName("テスト太郎");
    form.setRating("5");
    form.setComment("良い本でした");
    return form;
  }

  @Test
  void RFV_001_全項目正常() {
    // Given
    ReviewForm form = validForm();

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).isEmpty();
  }

  @Test
  void RFV_002_レビュアー名未入力() {
    // Given
    ReviewForm form = validForm();
    form.setReviewerName(null);

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("reviewerName");
    assertThat(errors.get("reviewerName")).isEqualTo("validation.required");
  }

  @Test
  void RFV_003_レビュアー名空白のみ() {
    // Given
    ReviewForm form = validForm();
    form.setReviewerName(" ");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("reviewerName");
  }

  @Test
  void RFV_004_レビュアー名最大長超過() {
    // Given: 51文字
    ReviewForm form = validForm();
    form.setReviewerName("あ".repeat(51));

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("reviewerName");
    assertThat(errors.get("reviewerName")).isEqualTo("validation.length.max");
  }

  @Test
  void RFV_005_レビュアー名最大長ちょうど() {
    // Given: 50文字
    ReviewForm form = validForm();
    form.setReviewerName("あ".repeat(50));

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("reviewerName");
  }

  @Test
  void RFV_006_評価未入力() {
    // Given
    ReviewForm form = validForm();
    form.setRating(null);

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("rating");
    assertThat(errors.get("rating")).isEqualTo("validation.required");
  }

  @Test
  void RFV_007_評価が数値でない() {
    // Given
    ReviewForm form = validForm();
    form.setRating("abc");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("rating");
    assertThat(errors.get("rating")).isEqualTo("validation.number.format");
  }

  @Test
  void RFV_008_評価が範囲未満() {
    // Given
    ReviewForm form = validForm();
    form.setRating("0");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("rating");
    assertThat(errors.get("rating")).isEqualTo("validation.number.range");
  }

  @Test
  void RFV_009_評価が範囲超過() {
    // Given
    ReviewForm form = validForm();
    form.setRating("6");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("rating");
  }

  @Test
  void RFV_010_評価が範囲内下限() {
    // Given
    ReviewForm form = validForm();
    form.setRating("1");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("rating");
  }

  @Test
  void RFV_011_評価が範囲内上限() {
    // Given
    ReviewForm form = validForm();
    form.setRating("5");

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("rating");
  }

  @Test
  void RFV_012_コメント最大長超過() {
    // Given: 1001文字
    ReviewForm form = validForm();
    form.setComment("a".repeat(1001));

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).containsKey("comment");
  }

  @Test
  void RFV_013_コメント未入力任意項目() {
    // Given
    ReviewForm form = validForm();
    form.setComment(null);

    // When
    Map<String, String> errors = ReviewFormValidator.validate(form);

    // Then
    assertThat(errors).doesNotContainKey("comment");
  }
}
