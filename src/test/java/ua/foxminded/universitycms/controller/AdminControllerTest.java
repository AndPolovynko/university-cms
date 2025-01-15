package ua.foxminded.universitycms.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ua.foxminded.universitycms.SpringSecurityConfig;
import ua.foxminded.universitycms.dto.UserResponse;
import ua.foxminded.universitycms.service.UserService;

@WebMvcTest(AdminController.class)
@Import(SpringSecurityConfig.class)
class AdminControllerTest {
  @Autowired
  private MockMvc mockMvc;
  
  @MockBean
  private UserService service;

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getAdminProfileShouldProvideAdminProfileView() throws Exception {
    when(service.getUserResponseByLogin(anyString())).thenReturn(UserResponse.builder().roles(new ArrayList<String>()).build());
    
    mockMvc.perform(get("/admin")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Your Admin Profile</title>")));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getAdminProfileShouldReturnForbiddenStatusIfUserIsNotAdmin() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
  }

  @Test
  void getAdminProfileShouldRedirectToLoginPageIfUserIsNotAuthorised() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
  }
}
