package jp.co.skig.training.bookshelf.util;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

/**
 * 単体テスト仕様書: ExceptionLogger_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class ExceptionLoggerTest {

  @Mock
  private MessageSource messageSource;

  @BeforeEach
  void setUp() {
    new MessageUtil(messageSource);
    lenient().when(messageSource.getMessage(org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
        .thenReturn("例外が発生しました: test error");
  }

  @Test
  void EL_001_例外ログが例外を投げずに完了する() {
    // Given
    RuntimeException exception = new RuntimeException("test error");

    // When & Then
    assertThatCode(() -> ExceptionLogger.log(exception)).doesNotThrowAnyException();
  }
}
