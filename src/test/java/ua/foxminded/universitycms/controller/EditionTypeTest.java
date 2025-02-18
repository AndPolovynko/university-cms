package ua.foxminded.universitycms.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EditionTypeTest {

  @Test
  void fromStringShouldReturnLoginInfoEnumConstantIfStringIsValid() {
    assertThat(EditionType.fromString("login-info")).isEqualTo(EditionType.LOGIN_INFO);
  }

  @Test
  void fromStringShouldReturnDetilsEnumConstantIfStringIsValid() {
    assertThat(EditionType.fromString("details")).isEqualTo(EditionType.DETAILS);
  }

  @Test
  void fromStringShouldReturnRolesEnumConstantIfStringIsValid() {
    assertThat(EditionType.fromString("roles")).isEqualTo(EditionType.ROLES);
  }

  @Test
  void fromStringShouldThrowIllegalArgumentExceptionIfStringIsInvalid() {
    Exception exeption = assertThrows(IllegalArgumentException.class, 
        () -> EditionType.fromString("invalid-type"));
    
    assertThat(exeption.getMessage()).contains("invalid-type");
  }
}
