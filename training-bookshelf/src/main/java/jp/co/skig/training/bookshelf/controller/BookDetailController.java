package jp.co.skig.training.bookshelf.controller;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Review;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.ReviewService;
import jp.co.skig.training.bookshelf.util.ExceptionLogger;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 書籍詳細コントローラー（BK02）
 */
@Controller
@RequiredArgsConstructor
public class BookDetailController {

  private final BookService bookService;
  private final ReviewService reviewService;

  /**
   * 書籍詳細とレビュー一覧を表示
   */
  @GetMapping("/book/detail/{bookId}")
  public String detail(@PathVariable Integer bookId, HttpSession session, Model model) {
    try {
      Book book = bookService.findById(bookId);
      if (book == null) {
        model.addAttribute("errorMessage", MessageUtil.getMessage("error.notfound.book"));
        model.addAttribute("redirectUrl", "/book/list");
        return "book/error";
      }

      List<Review> reviews = reviewService.findByBookId(bookId);
      double avgRating = reviews.stream()
          .mapToInt(Review::getRating)
          .average()
          .orElse(0);

      model.addAttribute("book", book);
      model.addAttribute("reviews", reviews);
      model.addAttribute("avgRating", Math.round(avgRating * 10) / 10.0);
      model.addAttribute("backSearchTitle", session.getAttribute(BookConstants.SESSION_SEARCH_TITLE));
      model.addAttribute("backSearchAuthor", session.getAttribute(BookConstants.SESSION_SEARCH_AUTHOR));
      model.addAttribute("backSearchCategoryId",
          session.getAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID));
      return "book/BK02_BookDetail";
    } catch (Exception e) {
      ExceptionLogger.log(e);
      model.addAttribute("errorMessage", MessageUtil.getMessage("common.system.error"));
      model.addAttribute("redirectUrl", "/book/list");
      return "book/error";
    }
  }
}
