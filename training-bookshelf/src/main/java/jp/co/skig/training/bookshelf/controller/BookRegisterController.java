package jp.co.skig.training.bookshelf.controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.form.BookRegisterForm;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 書籍登録コントローラー（BK03-BK05）
 */
@Controller
@RequiredArgsConstructor
public class BookRegisterController {

  private final BookService bookService;
  private final CategoryService categoryService;

  // ========================================
  // BK03: 書籍登録入力画面
  // ========================================

  /**
   * 書籍登録入力画面を表示
   */
  @GetMapping("/book/create")
  public String newForm(HttpSession session, Model model) {
    BookRegisterForm form = (BookRegisterForm) session.getAttribute(BookConstants.SESSION_REGISTER_FORM);
    model.addAttribute("bookRegisterForm", form != null ? form : new BookRegisterForm());
    model.addAttribute("categories", categoryService.findAll());
    return "book/BK03_BookRegisterInput";
  }

  // ========================================
  // BK04: 書籍登録確認画面
  // ========================================

  /**
   * 登録確認画面を表示（セッションの入力値から復元）
   */
  @GetMapping("/book/create/confirm")
  public String confirmRegister(HttpSession session, Model model) {
    BookRegisterForm form = (BookRegisterForm) session.getAttribute(BookConstants.SESSION_REGISTER_FORM);
    if (form == null) {
      return "redirect:/book/create";
    }
    model.addAttribute("bookRegisterForm", form);
    model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
    return "book/BK04_BookRegisterConfirm";
  }

  /**
   * 入力内容をバリデーションして確認画面へ遷移
   */
  @PostMapping("/book/create/confirm")
  public String confirmRegister(@ModelAttribute BookRegisterForm bookRegisterForm, HttpSession session,
      Model model) {
    Map<String, String> errors = BookFormValidator.validate(bookRegisterForm);
    if (!errors.isEmpty()) {
      model.addAttribute("bookRegisterForm", bookRegisterForm);
      model.addAttribute("categories", categoryService.findAll());
      model.addAttribute("errors", errors);
      return "book/BK03_BookRegisterInput";
    }

    session.setAttribute(BookConstants.SESSION_REGISTER_FORM, bookRegisterForm);
    return "redirect:/book/create/confirm";
  }

  /**
   * 書籍を登録する
   */
  @PostMapping("/book/create/register")
  public String register(HttpSession session, Model model) {
    BookRegisterForm form = (BookRegisterForm) session.getAttribute(BookConstants.SESSION_REGISTER_FORM);
    if (form == null) {
      return "redirect:/book/create";
    }

    try {
      if (bookService.isDuplicateIsbn(form.getIsbn())) {
        model.addAttribute("bookRegisterForm", form);
        model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
        model.addAttribute("errorMessage", MessageUtil.getMessage("validation.duplicate.isbn"));
        return "book/BK04_BookRegisterConfirm";
      }

      Book book = toBook(form);
      bookService.register(book);
      session.removeAttribute(BookConstants.SESSION_REGISTER_FORM);
      return "redirect:/book/create/complete?bookId=" + book.getBookId();
    } catch (Exception e) {
      ExceptionLogger.log(e);
      model.addAttribute("bookRegisterForm", form);
      model.addAttribute("categoryName", findCategoryName(form.getCategoryId()));
      model.addAttribute("errorMessage", MessageUtil.getMessage("db.error.insert"));
      return "book/BK04_BookRegisterConfirm";
    }
  }

  /**
   * 登録をキャンセルして一覧に戻る
   */
  @GetMapping("/book/create/cancel")
  public String cancelRegister(HttpSession session) {
    session.removeAttribute(BookConstants.SESSION_REGISTER_FORM);
    return "redirect:/book/list";
  }

  // ========================================
  // BK05: 書籍登録完了画面
  // ========================================

  /**
   * 登録完了画面を表示
   */
  @GetMapping("/book/create/complete")
  public String registerComplete(@RequestParam Integer bookId, Model model) {
    model.addAttribute("bookId", bookId);
    return "book/BK05_BookRegisterComplete";
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

  private Book toBook(BookRegisterForm form) {
    Book book = new Book();
    book.setTitle(form.getTitle());
    book.setAuthor(form.getAuthor());
    book.setPublisher(form.getPublisher());
    book.setPublishedDate(LocalDate.parse(form.getPublishedDate()));
    book.setIsbn(form.getIsbn());
    book.setCategoryId(Integer.valueOf(form.getCategoryId()));
    book.setPrice(Integer.valueOf(form.getPrice()));
    book.setDescription(form.getDescription());
    book.setIsRecommended(Boolean.TRUE.equals(form.getIsRecommended()));
    return book;
  }
}
