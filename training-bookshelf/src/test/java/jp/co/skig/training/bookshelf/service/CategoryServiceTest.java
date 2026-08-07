package jp.co.skig.training.bookshelf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import jp.co.skig.training.bookshelf.entity.Category;
import jp.co.skig.training.bookshelf.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 単体テスト仕様書: CategoryService_UT.md 準拠
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryService categoryService;

  private Category category(int id, String name) {
    Category category = new Category();
    category.setCategoryId(id);
    category.setCategoryName(name);
    return category;
  }

  @Test
  void CS_001_カテゴリ複数件取得() {
    // Given
    List<Category> categories = List.of(category(1, "小説"), category(2, "技術書"), category(3, "漫画"));
    when(categoryMapper.findAll()).thenReturn(categories);

    // When
    List<Category> actual = categoryService.findAll();

    // Then
    assertThat(actual).hasSize(3);
    assertThat(actual.get(0).getCategoryName()).isEqualTo("小説");
    assertThat(actual.get(1).getCategoryName()).isEqualTo("技術書");
    assertThat(actual.get(2).getCategoryName()).isEqualTo("漫画");
  }

  @Test
  void CS_002_カテゴリ0件() {
    // Given
    when(categoryMapper.findAll()).thenReturn(Collections.emptyList());

    // When
    List<Category> actual = categoryService.findAll();

    // Then
    assertThat(actual).isEmpty();
  }
}
