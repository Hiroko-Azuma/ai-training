package jp.co.skig.training.bookshelf.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 単体テスト仕様書: BookDeleteController_UT.md 準拠
 */
@WebMvcTest(BookDeleteController.class)
@Import(MessageUtil.class)
class BookDeleteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  private Book book(int id) {
    Book book = new Book();
    book.setBookId(id);
    book.setTitle("書籍" + id);
    return book;
  }

  @Test
  void BK09_001_書籍あり() throws Exception {
    // Given
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(get("/book/delete/confirm/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK09_BookDeleteConfirm"))
        .andExpect(model().attribute("book", book(1)));
  }

  @Test
  void BK09_002_書籍なし() throws Exception {
    // Given
    when(bookService.findById(999)).thenReturn(null);

    // When & Then
    mockMvc.perform(get("/book/delete/confirm/999"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "指定された書籍が見つかりません"));
  }

  @Test
  void BK09_003_削除成功() throws Exception {
    // Given
    when(bookService.delete(1)).thenReturn(1);

    // When & Then
    mockMvc.perform(post("/book/delete/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/book/delete/complete"));
  }

  @Test
  void BK09_004_対象なし() throws Exception {
    // Given
    when(bookService.delete(999)).thenReturn(0);

    // When & Then
    mockMvc.perform(post("/book/delete/999"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/error"))
        .andExpect(model().attribute("errorMessage", "指定された書籍が見つかりません"));
  }

  @Test
  void BK09_005_DBエラー() throws Exception {
    // Given
    when(bookService.delete(1)).thenThrow(new RuntimeException("db error"));
    when(bookService.findById(1)).thenReturn(book(1));

    // When & Then
    mockMvc.perform(post("/book/delete/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK09_BookDeleteConfirm"))
        .andExpect(model().attribute("errorMessage", "データの削除に失敗しました"));
  }

  @Test
  void BK10_001_完了画面表示() throws Exception {
    // When & Then
    mockMvc.perform(get("/book/delete/complete"))
        .andExpect(status().isOk())
        .andExpect(view().name("book/BK10_BookDeleteComplete"));
  }
}
