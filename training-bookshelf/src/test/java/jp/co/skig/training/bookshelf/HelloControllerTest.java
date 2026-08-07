package jp.co.skig.training.bookshelf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 単体テスト仕様書: HelloController_UT.md 準拠
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void HC_001_Hello文字列を返す() throws Exception {
    // Given & When & Then
    mockMvc.perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, World!"));
  }
}
