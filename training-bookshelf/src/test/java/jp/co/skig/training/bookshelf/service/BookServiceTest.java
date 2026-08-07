package jp.co.skig.training.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.mapper.BookMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 単体テスト仕様書: BookService_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookMapper bookMapper;

  @InjectMocks
  private BookService bookService;

  @Test
  void BS_001_ページオフセット計算() {
    // Given: 3ページ目、1ページ20件
    when(bookMapper.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(List.of());

    // When
    bookService.findAll(null, null, null, "bookId", "DESC", 2, 20);

    // Then: offset = page(2) * pageSize(20) = 40
    verify(bookMapper).findAll(null, null, null, "bookId", "DESC", 20, 40);
  }

  @Test
  void BS_002_検索条件をそのまま委譲() {
    // Given
    when(bookMapper.findAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(List.of());

    // When
    bookService.findAll("タイトル", "著者", 3, "title", "ASC", 0, 20);

    // Then
    verify(bookMapper).findAll(eq("タイトル"), eq("著者"), eq(3), eq("title"), eq("ASC"), eq(20), eq(0));
  }

  @Test
  void BS_003_件数取得成功() {
    // Given
    when(bookMapper.count(isNull(), isNull(), isNull())).thenReturn(5);

    // When
    int actual = bookService.count(null, null, null);

    // Then
    assertThat(actual).isEqualTo(5);
  }

  @Test
  void BS_004_存在する書籍を取得() {
    // Given
    Book expected = new Book();
    expected.setBookId(1);
    when(bookMapper.findById(1)).thenReturn(expected);

    // When
    Book actual = bookService.findById(1);

    // Then
    assertThat(actual).isSameAs(expected);
  }

  @Test
  void BS_005_存在しない書籍() {
    // Given
    when(bookMapper.findById(999)).thenReturn(null);

    // When
    Book actual = bookService.findById(999);

    // Then
    assertThat(actual).isNull();
  }

  @Test
  void BS_006_重複あり() {
    // Given
    Book existing = new Book();
    existing.setBookId(1);
    when(bookMapper.findByIsbn("123")).thenReturn(existing);

    // When
    boolean actual = bookService.isDuplicateIsbn("123");

    // Then
    assertThat(actual).isTrue();
  }

  @Test
  void BS_007_重複なし() {
    // Given
    when(bookMapper.findByIsbn("999")).thenReturn(null);

    // When
    boolean actual = bookService.isDuplicateIsbn("999");

    // Then
    assertThat(actual).isFalse();
  }

  @Test
  void BS_008_自身のISBN除外対象() {
    // Given
    Book existing = new Book();
    existing.setBookId(1);
    when(bookMapper.findByIsbn("123")).thenReturn(existing);

    // When
    boolean actual = bookService.isDuplicateIsbn("123", 1);

    // Then
    assertThat(actual).isFalse();
  }

  @Test
  void BS_009_他書籍と重複() {
    // Given
    Book existing = new Book();
    existing.setBookId(2);
    when(bookMapper.findByIsbn("123")).thenReturn(existing);

    // When
    boolean actual = bookService.isDuplicateIsbn("123", 1);

    // Then
    assertThat(actual).isTrue();
  }

  @Test
  void BS_010_重複なし_bookId指定() {
    // Given
    when(bookMapper.findByIsbn("999")).thenReturn(null);

    // When
    boolean actual = bookService.isDuplicateIsbn("999", 1);

    // Then
    assertThat(actual).isFalse();
  }

  @Test
  void BS_011_登録処理呼び出し() {
    // Given
    Book book = new Book();
    book.setTitle("テスト書籍");

    // When
    bookService.register(book);

    // Then
    verify(bookMapper, times(1)).insert(book);
  }

  @Test
  void BS_012_更新成功() {
    // Given
    Book book = new Book();
    when(bookMapper.update(book)).thenReturn(1);

    // When
    int actual = bookService.update(book);

    // Then
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void BS_013_楽観ロック失敗() {
    // Given
    Book book = new Book();
    when(bookMapper.update(book)).thenReturn(0);

    // When
    int actual = bookService.update(book);

    // Then
    assertThat(actual).isEqualTo(0);
  }

  @Test
  void BS_014_削除成功() {
    // Given
    when(bookMapper.delete(1)).thenReturn(1);

    // When
    int actual = bookService.delete(1);

    // Then
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void BS_015_対象なし() {
    // Given
    when(bookMapper.delete(999)).thenReturn(0);

    // When
    int actual = bookService.delete(999);

    // Then
    assertThat(actual).isEqualTo(0);
  }
}
