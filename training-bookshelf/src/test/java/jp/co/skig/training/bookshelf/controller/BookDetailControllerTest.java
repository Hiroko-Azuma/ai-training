package jp.co.skig.training.bookshelf.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Review;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.ReviewService;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 単体テスト仕様書: BookDetailController_UT.md 準拠
 */
@WebMvcTest(BookDetailController.class)
@Import(MessageUtil.class)
class BookDetailControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private ReviewService reviewService;

  private Review review(int rating) {
    Review review = new Review();
    review.setRating(rating);
    return review;
  }

  @Test
  void BK02_001_詳細表示_レビューあり() throws Exception {
    // Given: 書籍あり、レビュー2件(5,4)
    Book book = new Book();
    book.setBookId(1);
    when(bookService.findById(1)).thenReturn(book);
    when(reviewService.findByBookId(1)).thenReturn(List.of(review(5), review(4)));

    // When & Then: 平均 (5+4)/2 = 4.5
    mockMvc.perform(get("/book/detail/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK02_BookDetail"))
        .andExpect(model().attribute("avgRating", 4.5))
        .andExpect(model().attribute("reviews", List.of(review(5), review(4))));
  }

  @Test
  void BK02_002_詳細表示_レビューなし() throws Exception {
    // Given: 書籍あり、レビュー0件
    Book book = new Book();
    book.setBookId(1);
    when(bookService.findById(1)).thenReturn(book);
    when(reviewService.findByBookId(1)).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/detail/1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("avgRating", 0.0));
  }

  @Test
  void BK02_003_書籍が存在しない() throws Exception {
    // Given
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/book/detail/999"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "指定された書籍が見つかりません"))
        .andExpect(model().attribute("redirectUrl", "/book/list"));
  }

  @Test
  void BK02_004_DBエラー発生() throws Exception {
    // Given
    when(bookService.findById(1)).thenThrow(new RuntimeException("db error"));

    // When & Then
    mockMvc.perform(get("/book/detail/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。"));
  }

  @Test
  void BK02_005_検索条件をセッションから引継ぎ() throws Exception {
    // Given
    Book book = new Book();
    book.setBookId(1);
    when(bookService.findById(1)).thenReturn(book);
    when(reviewService.findByBookId(1)).thenReturn(Collections.emptyList());
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_SEARCH_TITLE, "検索タイトル");

    // When & Then
    mockMvc.perform(get("/book/detail/1").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("backSearchTitle", "検索タイトル"));
  }
}
