package jp.co.skig.training.bookshelf.controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.form.BookEditForm;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.CategoryService;
import jp.co.skig.training.bookshelf.util.BookFormValidator;
import jp.co.skig.training.bookshelf.util.ExceptionLogger;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 書籍編集コントローラー（BK06-BK08）
 */
@Controller
@RequiredArgsConstructor
public class BookEditController {

  private final BookService bookService;
  private final CategoryService categoryService;

  // ========================================
  // BK06: 書籍編集入力画面
  // ========================================

  /**
   * 書籍編集入力画面を表示
   */
  @GetMapping("/book/edit/{bookId}")
  public String editForm(@PathVariable Integer bookId, HttpSession session, Model model) {
    BookEditForm sessionForm = (BookEditForm) session.getAttribute(BookConstants.SESSION_EDIT_FORM);
    if (sessionForm != null && bookId.equals(sessionForm.getBookId())) {
      model.addAttribute("bookEditForm", sessionForm);
      model.addAttribute("categories", categoryService.findAll());
      return "book/BK06_BookEditInput";
    }

    Book book = bookService.findById(bookId);
    if (book == null) {
      model.addAttribute("errorMessage", MessageUtil.getMessage("error.notfound.book"));
      model.addAttribute("redirectUrl", "/book/list");
      return "book/error";
    }

    model.addAttribute("bookEditForm", toForm(book));
    model.addAttribute("categories", categoryService.findAll());
    return "book/BK06_BookEditInput";
  }

  // ========================================
  // BK07: 書籍編集確認画面
  // ========================================

  /**
   * 編集確認画面を表示（セッションの編集値から復元）
   */
  @GetMapping("/book/edit/{bookId}/confirm")
  public String confirmEdit(@PathVariable Integer bookId, HttpSession session, Model model) {
    BookEditForm form = (BookEditForm) session.getAttribute(BookConstants.SESSION_EDIT_FORM);
    if (form == null || !bookId.equals(form.getBookId())) {
      return "redirect:/book/edit/" + bookId;
    }
    model.addAttribute("bookEditForm", form);
    model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
    return "book/BK07_BookEditConfirm";
  }

  /**
   * 入力内容をバリデーションして確認画面へ遷移
   */
  @PostMapping("/book/edit/{bookId}/confirm")
  public String confirmEdit(@PathVariable Integer bookId,
      @ModelAttribute BookEditForm bookEditForm, HttpSession session, Model model) {
    bookEditForm.setBookId(bookId);
    Map<String, String> errors = BookFormValidator.validate(bookEditForm);
    if (!errors.isEmpty()) {
      model.addAttribute("bookEditForm", bookEditForm);
      model.addAttribute("categories", categoryService.findAll());
      model.addAttribute("errors", errors);
      return "book/BK06_BookEditInput";
    }

    session.setAttribute(BookConstants.SESSION_EDIT_FORM, bookEditForm);
    return "redirect:/book/edit/" + bookId + "/confirm";
  }

  /**
   * 書籍を更新する
   */
  @PostMapping("/book/edit/{bookId}/update")
  public String update(@PathVariable Integer bookId, HttpSession session, Model model) {
    BookEditForm form = (BookEditForm) session.getAttribute(BookConstants.SESSION_EDIT_FORM);
    if (form == null || !bookId.equals(form.getBookId())) {
      return "redirect:/book/edit/" + bookId;
    }

    try {
      if (bookService.isDuplicateIsbn(form.getIsbn(), bookId)) {
        model.addAttribute("bookEditForm", form);
        model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
        model.addAttribute("errorMessage", MessageUtil.getMessage("validation.duplicate.isbn"));
        return "book/BK07_BookEditConfirm";
      }

      Book book = toBook(form);
      int updateCount = bookService.update(book);
      if (updateCount == 0) {
        session.removeAttribute(BookConstants.SESSION_EDIT_FORM);
        model.addAttribute("errorMessage", MessageUtil.getMessage("error.concurrent.update"));
        model.addAttribute("redirectUrl", "/book/detail/" + bookId);
        return "book/error";
      }

      session.removeAttribute(BookConstants.SESSION_EDIT_FORM);
      return "redirect:/book/edit/complete?bookId=" + bookId;
    } catch (Exception e) {
      ExceptionLogger.log(e);
      model.addAttribute("bookEditForm", form);
      model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
      model.addAttribute("errorMessage", MessageUtil.getMessage("db.error.update"));
      return "book/BK07_BookEditConfirm";
    }
  }

  /**
   * 編集をキャンセルして詳細画面に戻る
   */
  @GetMapping("/book/edit/{bookId}/cancel")
  public String cancelEdit(@PathVariable Integer bookId, HttpSession session) {
    session.removeAttribute(BookConstants.SESSION_EDIT_FORM);
    return "redirect:/book/detail/" + bookId;
  }

  // ========================================
  // BK08: 書籍編集完了画面
  // ========================================

  /**
   * 更新完了画面を表示
   */
  @GetMapping("/book/edit/complete")
  public String updateComplete(@RequestParam Integer bookId, Model model) {
    model.addAttribute("bookId", bookId);
    return "book/BK08_BookEditComplete";
  }

  private String findCategoryName(String categoryId) {
    if (categoryId == null) {
      return null;
    }
    List<Category> categories = categoryService.findAll();
    return categories.stream()
        .filter(c -> c.getCategoryId().toString().equals(categoryId))
        .map(Category::getCategoryName)
        .findFirst()
        .orElse(null);
  }

  private BookEditForm toForm(Book book) {
    BookEditForm form = new BookEditForm();
    form.setBookId(book.getBookId());
    form.setTitle(book.getTitle());
    form.setAuthor(book.getAuthor());
    form.setPublisher(book.getPublisher());
    form.setPublishedDate(book.getPublishedDate().toString());
    form.setIsbn(book.getIsbn());
    form.setCategoryId(book.getCategoryId().toString());
    form.setPrice(book.getPrice().toString());
    form.setDescription(book.getDescription());
    form.setIsRecommended(book.getIsRecommended());
    form.setUpdatedAt(book.getUpdatedAt().toString());
    return form;
  }

  private Book toBook(BookEditForm form) {
    Book book = new Book();
    book.setBookId(form.getBookId());
    book.setTitle(form.getTitle());
    book.setAuthor(form.getAuthor());
    book.setPublisher(form.getPublisher());
    book.setPublishedDate(LocalDate.parse(form.getPublishedDate()));
    book.setIsbn(form.getIsbn());
    book.setCategoryId(Integer.valueOf(form.getCategoryId()));
    book.setPrice(Integer.valueOf(form.getPrice()));
    book.setDescription(form.getDescription());
    book.setIsRecommended(Boolean.TRUE.equals(form.getIsRecommended()));
    book.setUpdatedAt(LocalDateTime.parse(form.getUpdatedAt()));
    return book;
  }
}
