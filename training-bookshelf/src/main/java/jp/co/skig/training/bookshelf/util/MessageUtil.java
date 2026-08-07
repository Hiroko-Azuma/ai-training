package jp.co.skig.training.bookshelf.util;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

/**
 * メッセージ取得ユーティリティ
 */
@Component
public class MessageUtil {

  private static MessageSource messageSource;

  public MessageUtil(MessageSource messageSource) {
    MessageUtil.messageSource = messageSource;
  }

  /**
   * メッセージを取得する
   * @param code メッセージID
   * @param args 置換パラメータ
   * @return メッセージ本文
   */
  public static String getMessage(String code, Object... args) {
    try {
      return messageSource.getMessage(code, args, Locale.JAPANESE);
    } catch (NoSuchMessageException e) {
      return code;
    }
  }
}
