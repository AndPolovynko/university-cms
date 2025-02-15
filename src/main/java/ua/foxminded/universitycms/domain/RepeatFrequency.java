package ua.foxminded.universitycms.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RepeatFrequency {
  DAILY("daily"),
  WEEKLY("weekly"),
  MONTHLY("monthly");
  
  private final String value;

  public static RepeatFrequency fromString(String string) {
    for (RepeatFrequency frequency : RepeatFrequency.values()) {
        if (frequency.getValue().equalsIgnoreCase(string)) {
            return frequency;
        }
    }
    throw new IllegalArgumentException("Unknown repeat frequency: " + string);
  }
}
