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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.form.BookRegisterForm;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.CategoryService;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 単体テスト仕様書: BookRegisterController_UT.md 準拠
 */
@WebMvcTest(BookRegisterController.class)
@Import(MessageUtil.class)
class BookRegisterControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private CategoryService categoryService;

  private BookRegisterForm validForm() {
    BookRegisterForm form = new BookRegisterForm();
    form.setTitle("テスト書籍");
    form.setAuthor("著者");
    form.setPublisher("出版社");
    form.setPublishedDate(LocalDate.now().toString());
    form.setIsbn("1234567890");
    form.setCategoryId("1");
    form.setPrice("1000");
    form.setDescription("概要");
    return form;
  }

  @Test
  void BK03_001_初回表示() throws Exception {
    // Given: セッションにフォームなし
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK03_BookRegisterInput"))
        .andExpect(model().attribute("bookRegisterForm", new BookRegisterForm()));
  }

  @Test
  void BK03_002_再表示_セッション復元() throws Exception {
    // Given: セッションにフォームあり
    BookRegisterForm form = validForm();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/create").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("bookRegisterForm", form));
  }

  @Test
  void BK03_003_バリデーションエラー() throws Exception {
    // Given: title未入力
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/create/confirm")
        .param("title", "")
        .param("author", "著者")
        .param("publisher", "出版社")
        .param("publishedDate", LocalDate.now().toString())
        .param("isbn", "1234567890")
        .param("categoryId", "1")
        .param("price", "1000"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK03_BookRegisterInput"))
        .andExpect(model().attributeExists("errors"));
  }

  @Test
  void BK03_004_バリデーション成功() throws Exception {
    // Given: 全項目正常
    // When
    MvcResult result = mockMvc.perform(post("/book/create/confirm")
        .param("title", "テスト書籍")
        .param("author", "著者")
        .param("publisher", "出版社")
        .param("publishedDate", LocalDate.now().toString())
        .param("isbn", "1234567890")
        .param("categoryId", "1")
        .param("price", "1000"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/create/confirm"))
        .andReturn();

    // Then: セッションにフォームが保存される
    BookRegisterForm saved = (BookRegisterForm) result.getRequest().getSession()
        .getAttribute(BookConstants.SESSION_REGISTER_FORM);
    assertThat(saved.getTitle()).isEqualTo("テスト書籍");
  }

  @Test
  void BK04_001_セッションにフォームなし() throws Exception {
    // When & Then
    mockMvc.perform(get("/book/create/confirm"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/create"));
  }

  @Test
  void BK04_002_セッションにフォームあり() throws Exception {
    // Given
    BookRegisterForm form = validForm();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    Category category = new Category();
    category.setCategoryId(1);
    category.setCategoryName("小説");
    when(categoryService.findAll()).thenReturn(List.of(category));

    // When & Then
    mockMvc.perform(get("/book/create/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK04_BookRegisterConfirm"))
        .andExpect(model().attribute("categoryName", "小説"));
  }

  @Test
  void BK04_002b_カテゴリID未設定の場合はカテゴリ名null() throws Exception {
    // Given: フォームのカテゴリIDが未設定
    BookRegisterForm form = validForm();
    form.setCategoryId(null);
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);

    // When & Then
    mockMvc.perform(get("/book/create/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("categoryName", (Object) null));
  }

  @Test
  void BK04_003_セッションフォームなし_register() throws Exception {
    // When & Then
    mockMvc.perform(post("/book/create/register"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/create"));
  }

  @Test
  void BK04_004_ISBN重複() throws Exception {
    // Given
    BookRegisterForm form = validForm();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    when(bookService.isDuplicateIsbn("1234567890")).thenReturn(true);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/create/register").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK04_BookRegisterConfirm"))
        .andExpect(model().attribute("errorMessage", "このISBNは既に登録されています"));
  }

  @Test
  void BK04_005_登録成功() throws Exception {
    // Given
    BookRegisterForm form = validForm();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    when(bookService.isDuplicateIsbn("1234567890")).thenReturn(false);
    ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
    org.mockito.Mockito.doAnswer(invocation -> {
      Book book = invocation.getArgument(0);
      book.setBookId(10);
      return null;
    }).when(bookService).register(captor.capture());

    // When
    MvcResult result = mockMvc.perform(post("/book/create/register").session(session))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    // Then
    assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/book/create/complete?bookId=10");
    assertThat(result.getRequest().getSession().getAttribute(BookConstants.SESSION_REGISTER_FORM)).isNull();
    assertThat(captor.getValue().getIsRecommended()).isFalse();
  }

  @Test
  void BK04_005b_登録成功_お勧めフラグON() throws Exception {
    // Given: お勧めフラグをONで入力
    BookRegisterForm form = validForm();
    form.setIsRecommended(true);
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    when(bookService.isDuplicateIsbn("1234567890")).thenReturn(false);
    ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
    org.mockito.Mockito.doAnswer(invocation -> {
      Book book = invocation.getArgument(0);
      book.setBookId(11);
      return null;
    }).when(bookService).register(captor.capture());

    // When & Then
    mockMvc.perform(post("/book/create/register").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/create/complete?bookId=11"));

    // Then: お勧めフラグがtrueで登録される
    assertThat(captor.getValue().getIsRecommended()).isTrue();
  }

  @Test
  void BK04_006_DBエラー() throws Exception {
    // Given
    BookRegisterForm form = validForm();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, form);
    when(bookService.isDuplicateIsbn("1234567890")).thenReturn(false);
    org.mockito.Mockito.doThrow(new RuntimeException("db error")).when(bookService).register(any());
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/create/register").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK04_BookRegisterConfirm"))
        .andExpect(model().attribute("errorMessage", "データの登録に失敗しました"));
  }

  @Test
  void BK04_007_キャンセル() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, validForm());

    // When & Then
    mockMvc.perform(get("/book/create/cancel").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/list"));

    assertThat(session.getAttribute(BookConstants.SESSION_REGISTER_FORM)).isNull();
  }

  @Test
  void BK05_001_完了画面表示() throws Exception {
    // When & Then
    mockMvc.perform(get("/book/create/complete").param("bookId", "10"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK05_BookRegisterComplete"))
        .andExpect(model().attribute("bookId", 10));
  }
}
