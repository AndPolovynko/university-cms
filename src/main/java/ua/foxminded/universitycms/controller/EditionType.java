package ua.foxminded.universitycms.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EditionType {
  LOGIN_INFO("login-info"),
  ROLES("roles"),
  DETAILS("details");
  
  private final String value;

  public static EditionType fromString(String string) {
    for (EditionType type : EditionType.values()) {
        if (type.getValue().equalsIgnoreCase(string)) {
            return type;
        }
    }
    throw new IllegalArgumentException("Unknown edition type: " + string);
  }
}
