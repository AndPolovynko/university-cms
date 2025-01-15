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
import ua.foxminded.universitycms.service.UniversityClassService;
import ua.foxminded.universitycms.service.UserService;

@WebMvcTest(StudentController.class)
@Import(SpringSecurityConfig.class)
class StudentControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private UniversityClassService classService;

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStudentProfileShouldProvideStudentProfileView() throws Exception {
    when(userService.getUserResponseByLogin(anyString()))
        .thenReturn(UserResponse.builder().roles(new ArrayList<String>()).build());

    mockMvc.perform(get("/student")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Your Student Profile</title>")));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getStudentProfileShouldReturnForbiddenStatusIfUserIsNotStudent() throws Exception {
    mockMvc.perform(get("/student")).andExpect(status().isForbidden());
  }

  @Test
  void getStudentProfileShouldRedirectToLoginPageIfUserIsNotAuthorised() throws Exception {
    mockMvc.perform(get("/student")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStudentDayScheduleShouldReturnExpectedView() throws Exception {
    when(userService.getUserResponseByLogin(anyString()))
        .thenReturn(UserResponse.builder().groupName("group").build());

    mockMvc.perform(get("/student/timetable/today")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Your schedule for today:")));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStudentWeekScheduleShouldReturnExpectedView() throws Exception {
    when(userService.getUserResponseByLogin(anyString()))
        .thenReturn(UserResponse.builder().groupName("group").build());

    mockMvc.perform(get("/student/timetable/this-week")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Timetable")));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStudentMonthScheduleShouldReturnExpectedView() throws Exception {
    when(userService.getUserResponseByLogin(anyString()))
        .thenReturn(UserResponse.builder().groupName("group").build());

    mockMvc.perform(get("/student/timetable/this-month")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Timetable")));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStudentCustomScheduleShouldReturnExpectedViewIfRangeIsValid() throws Exception {
    when(userService.getUserResponseByLogin(anyString()))
        .thenReturn(UserResponse.builder().groupName("group").build());

    mockMvc.perform(get("/student/timetable/custom-range?customRange=2024-01-01_2024-02-02")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Timetable")));
  }
}
