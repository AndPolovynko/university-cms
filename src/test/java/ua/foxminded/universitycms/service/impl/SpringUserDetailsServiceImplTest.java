package ua.foxminded.universitycms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.UserPrincipal;
import ua.foxminded.universitycms.repository.UserRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class SpringUserDetailsServiceImplTest {
  @Mock
  UserRepository repo;
  
  @InjectMocks
  SpringUserDetailsServiceImpl service;
  
  @Test
  void loadUserByUsernameShouldReturnExpectedUserUserPrincipal() {
    User user = User.builder()
        .id("id")
        .login("test-login")
        .password("password")
        .email("email").build();
    
    when(repo.findByLogin("test-login")).thenReturn(Optional.of(user));
    
    assertThat(service.loadUserByUsername("test-login")).isEqualTo(new UserPrincipal(user));
  }
  
  @Test
  void loadUserByUsernameShouldThrowEntityNotFoundRuntimeExceptionIfUserDoesNotExist() {
    when(repo.findByLogin("test-login")).thenReturn(Optional.empty());
    
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.loadUserByUsername("test-login"));

    assertThat(exception.getMessage()).contains("test-login");
  }
}
