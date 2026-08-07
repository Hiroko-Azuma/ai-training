package jp.co.skig.training.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.entity.Review;
import jp.co.skig.training.bookshelf.mapper.ReviewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 単体テスト仕様書: ReviewService_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewService reviewService;

  private Review review(int id, String reviewerName, int rating) {
    Review review = new Review();
    review.setReviewId(id);
    review.setReviewerName(reviewerName);
    review.setRating(rating);
    return review;
  }

  @Test
  void RS_001_レビュー複数件取得() {
    // Given
    List<Review> reviews = List.of(review(1, "太郎", 5), review(2, "花子", 4));
    when(reviewMapper.findByBookId(1)).thenReturn(reviews);

    // When
    List<Review> actual = reviewService.findByBookId(1);

    // Then
    assertThat(actual).hasSize(2);
    assertThat(actual.get(0).getReviewerName()).isEqualTo("太郎");
    assertThat(actual.get(1).getReviewerName()).isEqualTo("花子");
  }

  @Test
  void RS_002_レビュー0件() {
    // Given
    when(reviewMapper.findByBookId(1)).thenReturn(Collections.emptyList());

    // When
    List<Review> actual = reviewService.findByBookId(1);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void RS_003_登録処理呼び出し() {
    // Given
    Review review = review(0, "太郎", 5);

    // When
    reviewService.register(review);

    // Then
    verify(reviewMapper, times(1)).insert(review);
  }
}
