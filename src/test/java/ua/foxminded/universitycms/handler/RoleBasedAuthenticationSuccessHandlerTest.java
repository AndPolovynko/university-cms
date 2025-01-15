package ua.foxminded.universitycms.handler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RoleBasedAuthenticationSuccessHandlerTest {
  @Mock
  HttpServletRequest request;
  @Mock
  HttpServletResponse response;
  @Mock
  Authentication authentication;
  
  RoleBasedAuthenticationSuccessHandler handeler = new RoleBasedAuthenticationSuccessHandler();

  @Test
  void onAuthenticationSuccessShouldSendRedirectToAdminPageIfUserRoleIsAdministrator() throws IOException, ServletException {
    when(authentication.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")));
    doNothing().when(response).sendRedirect(anyString());
    
    handeler.onAuthenticationSuccess(request, response, authentication);
    
    verify(response, atLeastOnce()).sendRedirect("/admin");
  }
  
  @Test
  void onAuthenticationSuccessShouldSendRedirectToTeacherPageIfUserRoleIsTeacher() throws IOException, ServletException {
    when(authentication.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_TEACHER")));
    doNothing().when(response).sendRedirect(anyString());
    
    handeler.onAuthenticationSuccess(request, response, authentication);
    
    verify(response, atLeastOnce()).sendRedirect("/teacher");
  }
  
  @Test
  void onAuthenticationSuccessShouldSendRedirectToStudentPageIfUserRoleIsStudent() throws IOException, ServletException {
    when(authentication.getAuthorities())
        .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
    doNothing().when(response).sendRedirect(anyString());
    
    handeler.onAuthenticationSuccess(request, response, authentication);
    
    verify(response, atLeastOnce()).sendRedirect("/student");
  }
}
