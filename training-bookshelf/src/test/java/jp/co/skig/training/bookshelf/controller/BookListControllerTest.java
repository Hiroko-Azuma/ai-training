package jp.co.skig.training.bookshelf.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
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
 * 単体テスト仕様書: BookListController_UT.md 準拠
 */
@WebMvcTest(BookListController.class)
@Import(MessageUtil.class)
class BookListControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private CategoryService categoryService;

  private List<Book> createBooks(int n) {
    List<Book> books = new java.util.ArrayList<>();
    for (int i = 1; i <= n; i++) {
      Book book = new Book();
      book.setBookId(i);
      book.setTitle("書籍" + i);
      books.add(book);
    }
    return books;
  }

  @Test
  void BK01_001_初期表示_0件() throws Exception {
    // Given: 書籍0件
    when(bookService.count(isNull(), isNull(), isNull())).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK01_BookList"))
        .andExpect(model().attribute("noDataMessage", "書籍が登録されていません"))
        .andExpect(model().attribute("books", Collections.emptyList()));
  }

  @Test
  void BK01_002_初期表示_データあり() throws Exception {
    // Given: 書籍3件
    when(bookService.count(isNull(), isNull(), isNull())).thenReturn(3);
    when(bookService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(createBooks(3));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list"))
        .andExpect(status().isOk())
        .andExpect(model().attributeDoesNotExist("noDataMessage"))
        .andExpect(model().attributeDoesNotExist("searchResultMessage"));

    var result = mockMvc.perform(get("/book/list")).andReturn();
    @SuppressWarnings("unchecked")
    List<Book> books = (List<Book>) result.getModelAndView().getModel().get("books");
    org.assertj.core.api.Assertions.assertThat(books).hasSize(3);
  }

  @Test
  void BK01_003_検索条件あり_結果あり() throws Exception {
    // Given: 該当書籍1件
    when(bookService.count(eq("Java"), isNull(), isNull())).thenReturn(1);
    when(bookService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(createBooks(1));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").param("searchTitle", "Java"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchResultMessage", "1件の書籍が見つかりました"));
  }

  @Test
  void BK01_004_ページ情報算出_端数あり() throws Exception {
    // Given: 該当書籍41件
    when(bookService.count(isNull(), isNull(), isNull())).thenReturn(41);
    when(bookService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(createBooks(20));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then: 1ページ20件 → 41件で 3ページに分割
    mockMvc.perform(get("/book/list").param("page", "1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("totalPages", 3))
        .andExpect(model().attribute("totalCount", 41));
  }

  @Test
  void BK01_005_ページ番号不正_0以下補正() throws Exception {
    // Given
    when(bookService.count(isNull(), isNull(), isNull())).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then: page=0 は 1 に補正される
    mockMvc.perform(get("/book/list").param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("currentPage", 1));
  }

  @Test
  void BK01_006_セッションの検索条件を利用() throws Exception {
    // Given: セッションに検索条件あり、リクエストパラメータなし
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_SEARCH_TITLE, "セッションタイトル");
    when(bookService.count(eq("セッションタイトル"), isNull(), isNull())).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchTitle", "セッションタイトル"));
  }

  @Test
  void BK01_007_リクエスト値でセッション更新() throws Exception {
    // Given: セッションに旧検索条件あり
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_SEARCH_TITLE, "旧タイトル");
    when(bookService.count(eq("新タイトル"), isNull(), isNull())).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When
    MvcResult result = mockMvc.perform(get("/book/list").session(session).param("searchTitle", "新タイトル"))
        .andExpect(status().isOk())
        .andReturn();

    // Then: セッションが新しい値に更新されている
    org.assertj.core.api.Assertions
        .assertThat(result.getRequest().getSession().getAttribute(BookConstants.SESSION_SEARCH_TITLE))
        .isEqualTo("新タイトル");
  }

  @Test
  void BK01_007b_カテゴリIDパラメータ直接指定() throws Exception {
    // Given: searchCategoryId をリクエストパラメータで直接指定
    when(bookService.count(isNull(), isNull(), eq(2))).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").param("searchCategoryId", "2"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchCategoryId", 2));
  }

  @Test
  void BK01_007c_ソート列_ソート順を直接指定() throws Exception {
    // Given: sortColumn / sortOrder をリクエストパラメータで直接指定
    when(bookService.count(isNull(), isNull(), isNull())).thenReturn(0);
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").param("sortColumn", "title").param("sortOrder", "ASC"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("sortColumn", "title"))
        .andExpect(model().attribute("sortOrder", "ASC"));
  }

  @Test
  void BK01_007d_著者のみで検索結果あり() throws Exception {
    // Given: searchAuthor のみ指定
    when(bookService.count(isNull(), eq("著者"), isNull())).thenReturn(1);
    when(bookService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(createBooks(1));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").param("searchAuthor", "著者"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchResultMessage", "1件の書籍が見つかりました"));
  }

  @Test
  void BK01_007e_カテゴリIDのみで検索結果あり() throws Exception {
    // Given: searchCategoryId のみ指定
    when(bookService.count(isNull(), isNull(), eq(3))).thenReturn(1);
    when(bookService.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(createBooks(1));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list").param("searchCategoryId", "3"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchResultMessage", "1件の書籍が見つかりました"));
  }

  @Test
  void BK01_008_DBエラー発生() throws Exception {
    // Given
    when(bookService.count(isNull(), isNull(), isNull())).thenThrow(new RuntimeException("db error"));
    when(categoryService.findAll()).thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/book/list"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK01_BookList"))
        .andExpect(model().attribute("errorMessage", "データの取得に失敗しました"));
  }

  @Test
  void BK01_009_検索条件クリア() throws Exception {
    // Given: セッションに検索条件あり
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(BookConstants.SESSION_SEARCH_TITLE, "タイトル");
    session.setAttribute(BookConstants.SESSION_SEARCH_AUTHOR, "著者");
    session.setAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID, 1);

    // When & Then
    mockMvc.perform(get("/book/list/clear").session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/list"));

    org.assertj.core.api.Assertions.assertThat(session.getAttribute(BookConstants.SESSION_SEARCH_TITLE))
        .isNull();
    org.assertj.core.api.Assertions.assertThat(session.getAttribute(BookConstants.SESSION_SEARCH_AUTHOR))
        .isNull();
    org.assertj.core.api.Assertions
        .assertThat(session.getAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID)).isNull();
  }
}
