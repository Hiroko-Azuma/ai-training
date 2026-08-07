package jp.co.skig.training.bookshelf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import jp.co.skig.training.bookshelf.constants.ReviewConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.form.ReviewForm;
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
import org.springframework.test.web.servlet.MvcResult;

/**
 * 単体テスト仕様書: ReviewController_UT.md 準拠
 */
@WebMvcTest(ReviewController.class)
@Import(MessageUtil.class)
class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private ReviewService reviewService;

  private Book book(int id) {
    Book book = new Book();
    book.setBookId(id);
    book.setTitle("書籍" + id);
    return book;
  }

  private ReviewForm reviewForm() {
    ReviewForm form = new ReviewForm();
    form.setReviewerName("テスト太郎");
    form.setRating("5");
    form.setComment("良い本でした");
    return form;
  }

  @Test
  void BK11_001_書籍なし() throws Exception {
    // Given
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/books/999/reviews/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。"));
  }

  @Test
  void BK11_002_初回表示() throws Exception {
    // Given
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(get("/books/1/reviews/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK11_ReviewPostInput"))
        .andExpect(model().attribute("reviewForm", new ReviewForm()));
  }

  @Test
  void BK11_003_再表示_セッション復元() throws Exception {
    // Given
    when(bookService.findById(1)).thenReturn(book(1));
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());

    // When & Then
    mockMvc.perform(get("/books/1/reviews/new").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("reviewForm", reviewForm()));
  }

  @Test
  void BK11_004_キャンセル() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());

    // When & Then
    mockMvc.perform(get("/books/1/reviews/cancel").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/detail/1"));

    assertThat(session.getAttribute(ReviewConstants.SESSION_REVIEW_FORM)).isNull();
  }

  @Test
  void BK12_001_セッションなし() throws Exception {
    // When & Then
    mockMvc.perform(get("/books/1/reviews/confirm"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books/1/reviews/new"));
  }

  @Test
  void BK12_002_書籍なし() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/books/999/reviews/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。"));
  }

  @Test
  void BK12_003_セッションあり() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(get("/books/1/reviews/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK12_ReviewPostConfirm"))
        .andExpect(model().attribute("reviewForm", reviewForm()));
  }

  @Test
  void BK12_004_バリデーションエラー() throws Exception {
    // Given: rating未入力
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(post("/books/1/reviews/confirm")
        .param("reviewerName", "テスト太郎")
        .param("rating", "")
        .param("comment", "コメント"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK11_ReviewPostInput"))
        .andExpect(model().attributeExists("errors"));
  }

  @Test
  void BK12_005_バリデーション成功() throws Exception {
    // When
    MvcResult result = mockMvc.perform(post("/books/1/reviews/confirm")
        .param("reviewerName", "テスト太郎")
        .param("rating", "5")
        .param("comment", "コメント"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books/1/reviews/confirm"))
        .andReturn();

    // Then
    ReviewForm saved = (ReviewForm) result.getRequest().getSession()
        .getAttribute(ReviewConstants.SESSION_REVIEW_FORM);
    assertThat(saved.getReviewerName()).isEqualTo("テスト太郎");
  }

  @Test
  void BK12_006_セッションなし_register() throws Exception {
    // When & Then
    mockMvc.perform(post("/books/1/reviews/register"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books/1/reviews/new"));
  }

  @Test
  void BK12_007_登録成功() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());

    // When
    MvcResult result = mockMvc.perform(post("/books/1/reviews/register").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books/1/reviews/complete"))
        .andReturn();

    // Then
    assertThat(result.getRequest().getSession().getAttribute(ReviewConstants.SESSION_REVIEW_FORM)).isNull();
    assertThat(result.getRequest().getSession().getAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME))
        .isEqualTo("テスト太郎");
    assertThat(result.getRequest().getSession().getAttribute(ReviewConstants.SESSION_COMPLETED_RATING))
        .isEqualTo("5");
  }

  @Test
  void BK12_008_DBエラー() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm());
    org.mockito.Mockito.doThrow(new RuntimeException("db error")).when(reviewService).register(any());
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(post("/books/1/reviews/register").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK12_ReviewPostConfirm"))
        .andExpect(model().attribute("errorMessage", "データの登録に失敗しました"));
  }

  @Test
  void BK13_001_書籍なし() throws Exception {
    // Given
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/books/999/reviews/complete"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。"));
  }

  @Test
  void BK13_002_完了画面表示() throws Exception {
    // Given
    when(bookService.findById(1)).thenReturn(book(1));
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME, "テスト太郎");
    session.setAttribute(ReviewConstants.SESSION_COMPLETED_RATING, "5");

    // When
    MvcResult result = mockMvc.perform(get("/books/1/reviews/complete").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK13_ReviewPostComplete"))
        .andExpect(model().attribute("reviewerName", "テスト太郎"))
        .andExpect(model().attribute("rating", "5"))
        .andReturn();

    // Then
    assertThat(result.getRequest().getSession().getAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME))
        .isNull();
    assertThat(result.getRequest().getSession().getAttribute(ReviewConstants.SESSION_COMPLETED_RATING)).isNull();
  }
}
