package ua.foxminded.universitycms.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import ua.foxminded.universitycms.SpringSecurityConfig;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;
import ua.foxminded.universitycms.service.GroupService;
import ua.foxminded.universitycms.service.UserService;
import ua.foxminded.universitycms.service.exception.InvalidUserConfigurationException;

@WebMvcTest(UserManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
class UserManagementControllerTest {
  private UserManagementController controller = new UserManagementController(null);
  
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService service;
  @MockBean
  private GroupService groupService;
  
  @Test
  void searchUsersShouldReturnExpectedPage() throws Exception {
      when(service.getUserResponses(anyString(), any(), any()))
          .thenReturn(new PageImpl<UserResponse>(getResponses()));
      
      mockMvc.perform(get("/admin/users/search"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("login")));
  }
  
  @Test
  void getUsersShouldReturnUserManagementView() throws Exception {
    when(service.getUserResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<UserResponse>(getResponses()));
    mockMvc.perform(get("/admin/users")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Users</title>")));
  }

  @Test
  void getUsersShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getUserResponses("keyword", 5, 0)).thenReturn(new PageImpl<UserResponse>(getResponses()));
    mockMvc.perform(get("/admin/users?keyword=keyword&pageNumber=1")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getUserResponses("keyword", 5, 0);
  }
  
  @Test
  void getUsersShouldDefaultToPageOneWhenPageNumberIsString() throws Exception {
      when(service.getUserResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/users?keyword=keyword&pageNumber=invalid"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getUserResponses("keyword", 5, 0);
  }

  @Test
  void getUsersShouldDefaultToPageOneWhenPageNumberIsNegative() throws Exception {
      when(service.getUserResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/users?keyword=keyword&pageNumber=-1"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getUserResponses("keyword", 5, 0);
  }

  @Test
  void getUsersShouldDefaultToPageOneWhenPageNumberIsZero() throws Exception {
      when(service.getUserResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/users?keyword=keyword&pageNumber=0"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getUserResponses("keyword", 5, 0);
  }
  
  @Test
  void getUsersShouldAddExpectedAttributesToModel() throws Exception {
    Page<UserResponse> pages = new PageImpl<>(getResponses());
    when(service.getUserResponses("keyword", 5, 0)).thenReturn(pages);

    mockMvc.perform(get("/admin/users?keyword=keyword&pageNumber=1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("users", pages.getContent()))
        .andExpect(model().attribute("currentPage", pages.getNumber() + 1))
        .andExpect(model().attribute("totalPages", pages.getTotalPages()))
        .andExpect(model().attribute("keyword", "keyword"));
  }

  @Test
  void getUserShouldReturnUserView() throws Exception {
    when(service.getUserResponseById("id")).thenReturn(getResponse());
    mockMvc.perform(get("/admin/users/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>User</title>")));
  }

  @Test
  void getUserShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getUserResponseById("id")).thenReturn(getResponse());

    mockMvc.perform(get("/admin/users/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getUserResponseById("id");
  }

  @Test
  void getUserShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getUserResponseById("id")).thenReturn(getResponse());

    mockMvc.perform(get("/admin/users/id")).andExpect(status().isOk())
        .andExpect(model().attribute("user", getResponse()));
  }

  @Test
  void deleteUserShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).deleteById("id");

    mockMvc.perform(delete("/admin/users/id").with(csrf())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).deleteById("id");
  }

  @Test
  void getEditUserViewShouldReturnUserEditView() throws Exception {
    when(service.getUserEditRequestById("id")).thenReturn(getEditRequest());
    mockMvc.perform(get("/admin/users/edit/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Edit User</title>")));
  }

  @Test
  void getEditUserViewShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getUserEditRequestById("id")).thenReturn(getEditRequest());

    mockMvc.perform(get("/admin/users/edit/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getUserEditRequestById("id");
  }

  @Test
  void getEditUserViewShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getUserEditRequestById("id")).thenReturn(getEditRequest());

    mockMvc.perform(get("/admin/users/edit/id")).andExpect(status().isOk())
        .andExpect(model().attribute("user", getEditRequest()));
  }

  @Test
  void updateUserShouldCallServiceMethodWithExpectedArgumentsIfEdditionTypeIsLoginInfo() throws Exception {
    doNothing().when(service).editLoginInfoFromRequest(getEditRequest());

    mockMvc.perform(put("/admin/users/edit/login-info/id").with(csrf()).flashAttr("user", getEditRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editLoginInfoFromRequest(getEditRequest());
  }

  @Test
  void updateUserShouldCallServiceMethodWithExpectedArgumentsIfEdditionTypeIsRoles() throws Exception {
    doNothing().when(service).editRolesAndDetailsFromRequest(getEditRequest());

    mockMvc.perform(put("/admin/users/edit/roles/id").with(csrf()).flashAttr("user", getEditRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editRolesAndDetailsFromRequest(getEditRequest());
  }

  @Test
  void updateUserShouldCallServiceMethodWithExpectedArgumentsIfEdditionTypeIsDetails() throws Exception {
    doNothing().when(service).editRolesAndDetailsFromRequest(getEditRequest());

    mockMvc.perform(put("/admin/users/edit/details/id").with(csrf()).flashAttr("user", getEditRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editRolesAndDetailsFromRequest(getEditRequest());
  }

  @Test
  void updateUserShouldShouldThrowExceptionIfEdditionTypeIsUndefined() throws Exception {
    ServletException exception = assertThrows(ServletException.class,
        () -> mockMvc.perform(put("/admin/users/edit/undefined-type/id").with(csrf()).flashAttr("user", getEditRequest())));

    assertThat(exception.getMessage()).contains("undefined-type");
  }

  @Test
  void getCreateUserViewShouldReturnUserCreateView() throws Exception {
    mockMvc.perform(get("/admin/users/create")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Create User</title>")));
  }

  @Test
  void getCreateUserViewShouldAddExpectedAttributesToModel() throws Exception {
    mockMvc.perform(get("/admin/users/create")).andExpect(status().isOk())
        .andExpect(model().attribute("user", UserCreateRequest.builder().build()));
  }

  @Test
  void createUserShouldCallService() throws Exception {
    when(service.saveFromRequest(getCreateRequest())).thenReturn(User.builder().build());
    when(service.findByLogin(any())).thenReturn(User.builder().build());

    mockMvc.perform(post("/admin/users/create").with(csrf()).flashAttr("user", getCreateRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getCreateRequest());
  }
  
  @Test
  void handleInvalidUserConfigurationExceptionShouldReturnExpectedModelAndView() {
    InvalidUserConfigurationException ex = new InvalidUserConfigurationException("Invalid user configuration.");
    ModelAndView modelAndView = controller.handleInvalidUserConfigurationException(ex);

    ModelAndView expectedModelAndView = new ModelAndView();
    expectedModelAndView.addObject("message", "Invalid user configuration.");
    expectedModelAndView.addObject("error", "Internal Server Error");
    expectedModelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    assertThat(modelAndView.getModel()).isEqualTo(expectedModelAndView.getModel());
  }

  private UserResponse getResponse() {
    return UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(List.of("Student", "Teacher", "Admin"))
        .firstName("Name")
        .lastName("Surname")
        .groupName("group")
        .jobTitle("job-title").build();
  }

  private List<UserResponse> getResponses() {
    List<UserResponse> responses = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responses.add(getResponse());
    }
    return responses;
  }

  private UserEditRequest getEditRequest() {
    return UserEditRequest.builder()
        .id("id")
        .login("login")
        .password("password")
        .email("email")
        .roles(List.of("Student", "Teacher", "Admin"))
        .firstName("Name")
        .lastName("Surname")
        .groupName("group")
        .jobTitle("job-title").build();
  }

  private UserCreateRequest getCreateRequest() {
    return UserCreateRequest.builder()
        .login("login")
        .password("password")
        .email("email")
        .roles(List.of("Student", "Teacher", "Admin"))
        .firstName("Name")
        .lastName("Surname")
        .groupName("group")
        .jobTitle("job-title").build();
  }
}
