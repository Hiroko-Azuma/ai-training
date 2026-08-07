package jp.co.skig.training.bookshelf.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import jp.co.skig.training.bookshelf.constants.ReviewConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Review;
import jp.co.skig.training.bookshelf.form.ReviewForm;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.ReviewService;
import jp.co.skig.training.bookshelf.util.ExceptionLogger;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import jp.co.skig.training.bookshelf.util.ReviewFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * レビュー投稿コントローラー（BK11-BK13）
 */
@Controller
@RequiredArgsConstructor
public class ReviewController {

  private final BookService bookService;
  private final ReviewService reviewService;

  // ========================================
  // BK11: レビュー投稿入力画面
  // ========================================

  /**
   * レビュー投稿入力画面を表示
   */
  @GetMapping("/books/{bookId}/reviews/new")
  public String newForm(@PathVariable Integer bookId, HttpSession session, Model model) {
    Book book = bookService.findById(bookId);
    if (book == null) {
      model.addAttribute("errorMessage", MessageUtil.getMessage("common.system.error"));
      model.addAttribute("redirectUrl", "/book/list");
      return "book/error";
    }

    ReviewForm form = (ReviewForm) session.getAttribute(ReviewConstants.SESSION_REVIEW_FORM);
    model.addAttribute("book", book);
    model.addAttribute("reviewForm", form != null ? form : new ReviewForm());
    return "book/BK11_ReviewPostInput";
  }

  /**
   * レビュー投稿をキャンセルして書籍詳細画面に戻る
   */
  @GetMapping("/books/{bookId}/reviews/cancel")
  public String cancelReview(@PathVariable Integer bookId, HttpSession session) {
    session.removeAttribute(ReviewConstants.SESSION_REVIEW_FORM);
    return "redirect:/book/detail/" + bookId;
  }

  // ========================================
  // BK12: レビュー投稿確認画面
  // ========================================

  /**
   * レビュー投稿確認画面を表示（セッションの入力値から復元）
   */
  @GetMapping("/books/{bookId}/reviews/confirm")
  public String confirmReview(@PathVariable Integer bookId, HttpSession session, Model model) {
    ReviewForm form = (ReviewForm) session.getAttribute(ReviewConstants.SESSION_REVIEW_FORM);
    if (form == null) {
      return "redirect:/books/" + bookId + "/reviews/new";
    }

    Book book = bookService.findById(bookId);
    if (book == null) {
      model.addAttribute("errorMessage", MessageUtil.getMessage("common.system.error"));
      model.addAttribute("redirectUrl", "/book/list");
      return "book/error";
    }

    model.addAttribute("book", book);
    model.addAttribute("reviewForm", form);
    return "book/BK12_ReviewPostConfirm";
  }

  /**
   * 入力内容をバリデーションして確認画面へ遷移
   */
  @PostMapping("/books/{bookId}/reviews/confirm")
  public String confirmReview(@PathVariable Integer bookId,
      @ModelAttribute ReviewForm reviewForm, HttpSession session, Model model) {
    Map<String, String> errors = ReviewFormValidator.validate(reviewForm);
    if (!errors.isEmpty()) {
      model.addAttribute("book", bookService.findById(bookId));
      model.addAttribute("reviewForm", reviewForm);
      model.addAttribute("errors", errors);
      return "book/BK11_ReviewPostInput";
    }

    session.setAttribute(ReviewConstants.SESSION_REVIEW_FORM, reviewForm);
    return "redirect:/books/" + bookId + "/reviews/confirm";
  }

  /**
   * レビューを登録する
   */
  @PostMapping("/books/{bookId}/reviews/register")
  public String postReview(@PathVariable Integer bookId, HttpSession session, Model model) {
    ReviewForm form = (ReviewForm) session.getAttribute(ReviewConstants.SESSION_REVIEW_FORM);
    if (form == null) {
      return "redirect:/books/" + bookId + "/reviews/new";
    }

    try {
      Review review = new Review();
      review.setBookId(bookId);
      review.setReviewerName(form.getReviewerName());
      review.setRating(Integer.valueOf(form.getRating()));
      review.setComment(form.getComment());
      reviewService.register(review);

      session.removeAttribute(ReviewConstants.SESSION_REVIEW_FORM);
      session.setAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME, form.getReviewerName());
      session.setAttribute(ReviewConstants.SESSION_COMPLETED_RATING, form.getRating());
      return "redirect:/books/" + bookId + "/reviews/complete";
    } catch (Exception e) {
      ExceptionLogger.log(e);
      model.addAttribute("book", bookService.findById(bookId));
      model.addAttribute("reviewForm", form);
      model.addAttribute("errorMessage", MessageUtil.getMessage("db.error.insert"));
      return "book/BK12_ReviewPostConfirm";
    }
  }

  // ========================================
  // BK13: レビュー投稿完了画面
  // ========================================

  /**
   * レビュー投稿完了画面を表示
   */
  @GetMapping("/books/{bookId}/reviews/complete")
  public String reviewComplete(@PathVariable Integer bookId, HttpSession session, Model model) {
    Book book = bookService.findById(bookId);
    if (book == null) {
      model.addAttribute("errorMessage", MessageUtil.getMessage("common.system.error"));
      model.addAttribute("redirectUrl", "/book/list");
      return "book/error";
    }

    model.addAttribute("book", book);
    model.addAttribute("reviewerName", session.getAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME));
    model.addAttribute("rating", session.getAttribute(ReviewConstants.SESSION_COMPLETED_RATING));
    session.removeAttribute(ReviewConstants.SESSION_COMPLETED_REVIEWER_NAME);
    session.removeAttribute(ReviewConstants.SESSION_COMPLETED_RATING);
    return "book/BK13_ReviewPostComplete";
  }
}
