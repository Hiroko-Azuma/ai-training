package jp.co.skig.training.bookshelf.service;

import java.util.List;
import jp.co.skig.training.bookshelf.entity.Review;
import jp.co.skig.training.bookshelf.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * レビューサービス
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewMapper reviewMapper;

  /**
   * 指定書籍IDのレビュー一覧を取得する（投稿日時降順）
   * @param bookId 書籍ID
   * @return レビュー一覧
   */
  public List<Review> findByBookId(Integer bookId) {
    return reviewMapper.findByBookId(bookId);
  }

  /**
   * レビューを登録する
   * @param review レビュー情報
   */
  @Transactional
  public void register(Review review) {
    reviewMapper.insert(review);
  }
}
