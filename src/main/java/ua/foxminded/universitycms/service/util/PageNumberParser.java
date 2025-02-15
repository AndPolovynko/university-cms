package ua.foxminded.universitycms.service.util;

import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageNumberParser {
  public static final Integer DEFAULT_PAGE_NUMBER = 0;

  public static Integer parse(String value) {
    return Optional.ofNullable(value)
        .filter(param -> param.matches("\\d+"))
        .map(Integer::parseInt)
        .filter(num -> num > 0)
        .map(num -> num - 1)
        .orElse(DEFAULT_PAGE_NUMBER);
  }
}
