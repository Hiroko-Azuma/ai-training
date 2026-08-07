package jp.co.skig.training.bookshelf.controller;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import jp.co.skig.training.bookshelf.constants.BookConstants;
import jp.co.skig.training.bookshelf.entity.Book;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.service.BookService;
import jp.co.skig.training.bookshelf.service.CategoryService;
import jp.co.skig.training.bookshelf.util.ExceptionLogger;
import jp.co.skig.training.bookshelf.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 書籍一覧コントローラー（BK01）
 */
@Controller
@RequiredArgsConstructor
public class BookListController {

  private final BookService bookService;
  private final CategoryService categoryService;

  /**
   * 書籍一覧を表示（検索・ソート・ページングを含む）
   */
  @GetMapping("/book/list")
  public String list(
      @RequestParam(required = false) String searchTitle,
      @RequestParam(required = false) String searchAuthor,
      @RequestParam(required = false) Integer searchCategoryId,
      @RequestParam(required = false) String searchPublisher,
      @RequestParam(required = false) String sortColumn,
      @RequestParam(required = false) String sortOrder,
      @RequestParam(defaultValue = "1") int page,
      HttpSession session,
      Model model) {

    // 検索条件確定：リクエスト指定があればセッションを更新、なければセッション値を利用
    String title = resolveSearchValue(searchTitle, session, BookConstants.SESSION_SEARCH_TITLE);
    String author = resolveSearchValue(searchAuthor, session, BookConstants.SESSION_SEARCH_AUTHOR);
    Integer categoryId = searchCategoryId != null
        ? searchCategoryId
        : (Integer) session.getAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID);
    String publisher = resolveSearchValue(searchPublisher, session,
        BookConstants.SESSION_SEARCH_PUBLISHER);

    session.setAttribute(BookConstants.SESSION_SEARCH_TITLE, title);
    session.setAttribute(BookConstants.SESSION_SEARCH_AUTHOR, author);
    session.setAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID, categoryId);
    session.setAttribute(BookConstants.SESSION_SEARCH_PUBLISHER, publisher);

    String column = sortColumn != null ? sortColumn : BookConstants.DEFAULT_SORT_COLUMN;
    String order = sortOrder != null ? sortOrder : BookConstants.DEFAULT_SORT_ORDER;
    int currentPage = Math.max(1, page);

    try {
      int totalCount = bookService.count(title, author, categoryId, publisher);
      int totalPages = (int) Math.ceil((double) totalCount / BookConstants.PAGE_SIZE);
      List<Book> books = bookService.findAll(title, author, categoryId, publisher, column, order,
          currentPage - 1, BookConstants.PAGE_SIZE);
      List<Category> categories = categoryService.findAll();
      List<String> publishers = bookService.findDistinctPublishers();

      model.addAttribute("books", books);
      model.addAttribute("categories", categories);
      model.addAttribute("publishers", publishers);
      model.addAttribute("searchTitle", title);
      model.addAttribute("searchAuthor", author);
      model.addAttribute("searchCategoryId", categoryId);
      model.addAttribute("searchPublisher", publisher);
      model.addAttribute("sortColumn", column);
      model.addAttribute("sortOrder", order);
      model.addAttribute("currentPage", currentPage);
      model.addAttribute("totalPages", totalPages);
      model.addAttribute("totalCount", totalCount);

      if (totalCount == 0) {
        model.addAttribute("noDataMessage", MessageUtil.getMessage("bk01.message.nodata"));
      } else if ((title != null && !title.isBlank()) || (author != null && !author.isBlank())
          || categoryId != null || (publisher != null && !publisher.isBlank())) {
        model.addAttribute("searchResultMessage",
            MessageUtil.getMessage("bk01.message.searchresult", totalCount));
      }
    } catch (Exception e) {
      ExceptionLogger.log(e);
      model.addAttribute("errorMessage", MessageUtil.getMessage("db.error.select"));
    }

    return "book/BK01_BookList";
  }

  /**
   * 検索条件をクリアして一覧にリダイレクトする
   */
  @GetMapping("/book/list/clear")
  public String clearSearch(HttpSession session) {
    session.removeAttribute(BookConstants.SESSION_SEARCH_TITLE);
    session.removeAttribute(BookConstants.SESSION_SEARCH_AUTHOR);
    session.removeAttribute(BookConstants.SESSION_SEARCH_CATEGORY_ID);
    session.removeAttribute(BookConstants.SESSION_SEARCH_PUBLISHER);
    return "redirect:/book/list";
  }

  private String resolveSearchValue(String requestValue, HttpSession session, String sessionKey) {
    if (requestValue != null) {
      return requestValue;
    }
    Object sessionValue = session.getAttribute(sessionKey);
    return sessionValue != null ? sessionValue.toString() : null;
  }
}
