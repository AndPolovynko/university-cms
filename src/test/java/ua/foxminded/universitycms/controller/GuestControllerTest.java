package ua.foxminded.universitycms.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ua.foxminded.universitycms.SpringSecurityConfig;

@WebMvcTest(GuestController.class)
@Import(SpringSecurityConfig.class)
class GuestControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void getWelcomViewShouldProvideGuestWelcomeView() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Welcome Page</title>")));
  }

  @Test
  @WithMockUser(roles = "USER")
  void getWelcomViewShouldReturnGuestViewForUndefinedUser() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Welcome Page</title>")));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getWelcomViewShouldRedirectAdministrator() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin"));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getWelcomViewShouldRedirectStudent() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/student"));
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void getWelcomViewShouldRedirectTeacher() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/teacher"));
  }

  @Test
  void getLoginViewShouldProvideLoginView() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Log In</title>")));
  }

  @Test
  @WithMockUser(roles = "USER")
  void getLoginViewShouldReturnGuestViewForUndefinedUser() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Log In</title>")));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getLoginViewShouldRedirectAdministrator() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin"));
  }

  @Test
  @WithMockUser(roles = "TEACHER")
  void getLoginViewShouldRedirectTeacher() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/teacher"));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getLoginViewShouldRedirectStudent() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/student"));
  }
  
  @Test
  void getWelcomViewShouldProvideGuestWelcomeViewInEnglishIfRequired() throws Exception {
    mockMvc.perform(get("/?lang=en")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Welcome Page</title>")));
  }
  
  @Test
  void getWelcomViewShouldProvideGuestWelcomeViewInUkrainianIfRequired() throws Exception {
    mockMvc.perform(get("/?lang=ua")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Сторінка привітання</title>")));
  }
  
  @Test
  void getWelcomViewShouldProvideGuestWelcomeViewInEnglishIfRequiredLanguageIsAbsent() throws Exception {
    mockMvc.perform(get("/?lang=abc")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Welcome Page</title>")));
  }
}
