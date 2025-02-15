package ua.foxminded.universitycms.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

public class PageNumberParserTest {
  
  @Test
  void pageNumberParserShouldThrowUnsupportedOperationExceptionIfInstantiationIsAttempted() {
    Exception exception = assertThrows(InvocationTargetException.class, () -> {
      Constructor<PageNumberParser> constructor = PageNumberParser.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    });
    
    assertThat(exception.getCause() instanceof UnsupportedOperationException);
  }

  @Test
  void parseShouldReturnDefaultPageNumberIfArgumentIsNull() {
    assertThat(PageNumberParser.parse(null)).isEqualTo(PageNumberParser.DEFAULT_PAGE_NUMBER);
  }

  @Test
  void parseShouldReturnDefaultPageNumberIfArgumentIsNotNumeric() {
    assertThat(PageNumberParser.parse("abc")).isEqualTo(PageNumberParser.DEFAULT_PAGE_NUMBER);
  }

  @Test
  void parseShouldReturnDefaultPageNumberIfArgumentIsArgumentSmallerThanOne() {
    assertThat(PageNumberParser.parse("-354")).isEqualTo(PageNumberParser.DEFAULT_PAGE_NUMBER);
  }

  @Test
  void parseShouldReturnPageNumberSubtractedByOneIfArgumentIsValid() {
    String pageNumber = "5";

    assertThat(PageNumberParser.parse(pageNumber)).isEqualTo(Integer.valueOf(pageNumber) - 1);
  }
}
