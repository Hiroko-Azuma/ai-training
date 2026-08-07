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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.form.BookEditForm;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.CategoryService;
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
 * 単体テスト仕様書: BookEditController_UT.md 準拠
 */
@WebMvcTest(BookEditController.class)
@Import(MessageUtil.class)
class BookEditControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private CategoryService categoryService;

  private Book book(int id) {
    Book book = new Book();
    book.setBookId(id);
    book.setTitle("書籍" + id);
    book.setAuthor("著者");
    book.setPublisher("出版社");
    book.setPublishedDate(LocalDate.now());
    book.setIsbn("1234567890");
    book.setCategoryId(1);
    book.setPrice(1000);
    book.setDescription("概要");
    book.setUpdatedAt(LocalDateTime.now());
    return book;
  }

  private BookEditForm editForm(int bookId) {
    BookEditForm form = new BookEditForm();
    form.setBookId(bookId);
    form.setTitle("編集書籍");
    form.setAuthor("著者");
    form.setPublisher("出版社");
    form.setPublishedDate(LocalDate.now().toString());
    form.setIsbn("1234567890");
    form.setCategoryId("1");
    form.setPrice("1000");
    form.setUpdatedAt(LocalDateTime.now().toString());
    return form;
  }

  @Test
  void BK06_001_セッション復元_同一bookId() throws Exception {
    // Given
    BookEditForm form = editForm(1);
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, form);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/edit/1").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK06_BookEditInput"))
        .andExpect(model().attribute("bookEditForm", form));
  }

  @Test
  void BK06_002_DBから初期表示() throws Exception {
    // Given: セッションなし、書籍あり
    when(bookService.findById(1)).thenReturn(book(1));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When
    MvcResult result = mockMvc.perform(get("/book/edit/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK06_BookEditInput"))
        .andReturn();

    // Then
    BookEditForm form = (BookEditForm) result.getModelAndView().getModel().get("bookEditForm");
    assertThat(form.getBookId()).isEqualTo(1);
    assertThat(form.getTitle()).isEqualTo("書籍1");
  }

  @Test
  void BK06_003_書籍が存在しない() throws Exception {
    // Given
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/book/edit/999"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "指定された書籍が見つかりません"));
  }

  @Test
  void BK06_004_セッションのbookIdが不一致の場合はDBから取得() throws Exception {
    // Given: セッションには別bookIdのフォームが存在
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(2));
    when(bookService.findById(1)).thenReturn(book(1));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When
    MvcResult result = mockMvc.perform(get("/book/edit/1").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK06_BookEditInput"))
        .andReturn();

    // Then: DBの値からフォームが生成される
    BookEditForm form = (BookEditForm) result.getModelAndView().getModel().get("bookEditForm");
    assertThat(form.getBookId()).isEqualTo(1);
    assertThat(form.getTitle()).isEqualTo("書籍1");
  }

  @Test
  void BK07_001_セッションなし() throws Exception {
    // When & Then
    mockMvc.perform(get("/book/edit/1/confirm"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/1"));
  }

  @Test
  void BK07_002_bookId不一致() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(2));

    // When & Then
    mockMvc.perform(get("/book/edit/1/confirm").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/1"));
  }

  @Test
  void BK07_003_セッションあり_一致() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));
    Category category = new Category();
    category.setCategoryId(1);
    category.setCategoryName("小説");
    when(categoryService.findAll()).thenReturn(List.of(category));

    // When & Then
    mockMvc.perform(get("/book/edit/1/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK07_BookEditConfirm"))
        .andExpect(model().attribute("categoryName", "小説"));
  }

  @Test
  void BK07_003b_カテゴリID未設定の場合はカテゴリ名null() throws Exception {
    // Given: フォームのカテゴリIDが未設定
    BookEditForm form = editForm(1);
    form.setCategoryId(null);
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, form);

    // When & Then
    mockMvc.perform(get("/book/edit/1/confirm").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("categoryName", (Object) null));
  }

  @Test
  void BK07_004_バリデーションエラー() throws Exception {
    // Given: title未入力
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/edit/1/confirm")
        .param("title", "")
        .param("author", "著者")
        .param("publisher", "出版社")
        .param("publishedDate", LocalDate.now().toString())
        .param("isbn", "1234567890")
        .param("categoryId", "1")
        .param("price", "1000"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK06_BookEditInput"))
        .andExpect(model().attributeExists("errors"));
  }

  @Test
  void BK07_005_バリデーション成功() throws Exception {
    // When
    MvcResult result = mockMvc.perform(post("/book/edit/1/confirm")
        .param("title", "編集書籍")
        .param("author", "著者")
        .param("publisher", "出版社")
        .param("publishedDate", LocalDate.now().toString())
        .param("isbn", "1234567890")
        .param("categoryId", "1")
        .param("price", "1000"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/1/confirm"))
        .andReturn();

    // Then
    BookEditForm saved = (BookEditForm) result.getRequest().getSession()
        .getAttribute(BookConstants.SESSION_EDIT_FORM);
    assertThat(saved.getBookId()).isEqualTo(1);
    assertThat(saved.getTitle()).isEqualTo("編集書籍");
  }

  @Test
  void BK07_006_セッションなし_update() throws Exception {
    // When & Then
    mockMvc.perform(post("/book/edit/1/update"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/1"));
  }

  @Test
  void BK07_006b_セッションのbookId不一致_update() throws Exception {
    // Given: セッションには別bookIdのフォームが存在
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(2));

    // When & Then
    mockMvc.perform(post("/book/edit/1/update").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/1"));
  }

  @Test
  void BK07_007_ISBN重複() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));
    when(bookService.isDuplicateIsbn("1234567890", 1)).thenReturn(true);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/edit/1/update").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK07_BookEditConfirm"))
        .andExpect(model().attribute("errorMessage", "このISBNは既に登録されています"));
  }

  @Test
  void BK07_008_楽観ロック失敗() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));
    when(bookService.isDuplicateIsbn("1234567890", 1)).thenReturn(false);
    when(bookService.update(any())).thenReturn(0);

    // When
    MvcResult result = mockMvc.perform(post("/book/edit/1/update").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "他のユーザーによって更新されています。最新のデータを取得してください。"))
        .andReturn();

    // Then
    assertThat(result.getRequest().getSession().getAttribute(BookConstants.SESSION_EDIT_FORM)).isNull();
  }

  @Test
  void BK07_009_更新成功() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));
    when(bookService.isDuplicateIsbn("1234567890", 1)).thenReturn(false);
    when(bookService.update(any())).thenReturn(1);

    // When
    MvcResult result = mockMvc.perform(post("/book/edit/1/update").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/edit/complete?bookId=1"))
        .andReturn();

    // Then
    assertThat(result.getRequest().getSession().getAttribute(BookConstants.SESSION_EDIT_FORM)).isNull();
  }

  @Test
  void BK07_010_DBエラー() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));
    when(bookService.isDuplicateIsbn("1234567890", 1)).thenReturn(false);
    when(bookService.update(any())).thenThrow(new RuntimeException("db error"));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(post("/book/edit/1/update").session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK07_BookEditConfirm"))
        .andExpect(model().attribute("errorMessage", "データの更新に失敗しました"));
  }

  @Test
  void BK07_011_キャンセル() throws Exception {
    // Given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_EDIT_FORM, editForm(1));

    // When & Then
    mockMvc.perform(get("/book/edit/1/cancel").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/detail/1"));

    assertThat(session.getAttribute(BookConstants.SESSION_EDIT_FORM)).isNull();
  }

  @Test
  void BK08_001_完了画面表示() throws Exception {
    // When & Then
    mockMvc.perform(get("/book/edit/complete").param("bookId", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK08_BookEditComplete"))
        .andExpect(model().attribute("bookId", 1));
  }
}
