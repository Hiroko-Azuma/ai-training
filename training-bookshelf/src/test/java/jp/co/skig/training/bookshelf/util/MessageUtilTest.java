package jp.co.skig.training.bookshelf.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * 単体テスト仕様書: MessageUtil_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

  @Mock
  private MessageSource messageSource;

  @Test
  void getMessage_001_正常取得() {
    // Given
    new MessageUtil(messageSource);
    when(messageSource.getMessage("validation.required", new Object[] { "タイトル" }, Locale.JAPANESE))
        .thenReturn("タイトルは必須です");

    // When
    String actual = MessageUtil.getMessage("validation.required", "タイトル");

    // Then
    assertThat(actual).isEqualTo("タイトルは必須です");
  }

  @Test
  void getMessage_002_メッセージ未存在時はコードをそのまま返す() {
    // Given
    new MessageUtil(messageSource);
    when(messageSource.getMessage("no.such.code", new Object[0], Locale.JAPANESE))
        .thenThrow(new NoSuchMessageException("no.such.code"));

    // When
    String actual = MessageUtil.getMessage("no.such.code");

    // Then
    assertThat(actual).isEqualTo("no.such.code");
  }
}
