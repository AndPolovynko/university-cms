package ua.foxminded.universitycms.controller;

import static org.hamcrest.Matchers.containsString;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ua.foxminded.universitycms.SpringSecurityConfig;
import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;
import ua.foxminded.universitycms.service.CourseService;

@WebMvcTest(CourseManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
class CourseManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CourseService service;
  
  @Test
  void searchCoursesShouldReturnExpectedResults() throws Exception {
    String keyword = "Course";
    int fastSearchLimit = 5;
    List<CourseResponse> expectedResponses = List.of(
        CourseResponse.builder().id("1").name("Course 1").build(),
        CourseResponse.builder().id("2").name("Course 2").build());

    when(service.getCourseResponses(keyword, fastSearchLimit, 0)).thenReturn(new PageImpl<>(expectedResponses));

    mockMvc.perform(get("/admin/courses/search")
        .param("keyword", keyword).param("fastSearchLimit", String.valueOf(fastSearchLimit)))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":\"1\",\"name\":\"Course 1\"},{\"id\":\"2\",\"name\":\"Course 2\"}]"));

    verify(service).getCourseResponses(keyword, fastSearchLimit, 0);
  }
  
  @Test
  void getCoursesShouldReturnCourseManagementView() throws Exception {
    when(service.getCourseResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<CourseResponse>(getResponses()));
    mockMvc.perform(get("/admin/courses")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Courses</title>")));
  }

  @Test
  void getCoursesShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getCourseResponses("keyword", 5, 0)).thenReturn(new PageImpl<CourseResponse>(getResponses()));
    mockMvc.perform(get("/admin/courses?keyword=keyword&pageNumber=1")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getCourseResponses("keyword", 5, 0);
  }

  @Test
  void getCoursesShouldDefaultToPageOneWhenPageNumberIsString() throws Exception {
      when(service.getCourseResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/courses?keyword=keyword&pageNumber=invalid"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getCourseResponses("keyword", 5, 0);
  }

  @Test
  void getCoursesShouldDefaultToPageOneWhenPageNumberIsNegative() throws Exception {
      when(service.getCourseResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/courses?keyword=keyword&pageNumber=-1"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getCourseResponses("keyword", 5, 0);
  }

  @Test
  void getCoursesShouldDefaultToPageOneWhenPageNumberIsZero() throws Exception {
      when(service.getCourseResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/courses?keyword=keyword&pageNumber=0"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getCourseResponses("keyword", 5, 0);
  }
  
  @Test
  void getCoursesShouldAddExpectedAttributesToModel() throws Exception {
    Page<CourseResponse> pages = new PageImpl<>(getResponses());
    when(service.getCourseResponses("keyword", 5, 0)).thenReturn(pages);
    
    mockMvc.perform(get("/admin/courses?keyword=keyword&pageNumber=1"))
      .andExpect(status().isOk())
      .andExpect(model().attribute("courses", pages.getContent()))
      .andExpect(model().attribute("currentPage", pages.getNumber() + 1))
      .andExpect(model().attribute("totalPages", pages.getTotalPages()))
      .andExpect(model().attribute("keyword", "keyword"));
  }
  
  @Test
  void getCourseShouldReturnCourseView() throws Exception {
    when(service.getCourseResponseById("id")).thenReturn(getResponse());
    mockMvc.perform(get("/admin/courses/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Course</title>")));
  }
  
  @Test
  void getCourseShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getCourseResponseById("id")).thenReturn(getResponse());
    
    mockMvc.perform(get("/admin/courses/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getCourseResponseById("id");
  }
  
  @Test
  void getCourseShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getCourseResponseById("id")).thenReturn(getResponse());
    
    mockMvc.perform(get("/admin/courses/id")).andExpect(status().isOk())
      .andExpect(model().attribute("course", getResponse()));
  }
  
  @Test
  void deleteCourseShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).deleteById("id");
    
    mockMvc.perform(delete("/admin/courses/id").with(csrf())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).deleteById("id");
  }
  
  @Test
  void getEditCourseViewShouldReturnCourseEditView() throws Exception {
    when(service.getCourseEditRequestById("id")).thenReturn(getEditRequest());
    mockMvc.perform(get("/admin/courses/edit/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Edit Course</title>")));
  }
  
  @Test
  void getEditCourseViewShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getCourseEditRequestById("id")).thenReturn(getEditRequest());
    
    mockMvc.perform(get("/admin/courses/edit/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getCourseEditRequestById("id");
  }
  
  @Test
  void getEditCourseViewShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getCourseEditRequestById("id")).thenReturn(getEditRequest());
    
    mockMvc.perform(get("/admin/courses/edit/id")).andExpect(status().isOk())
      .andExpect(model().attribute("course", getEditRequest()));
  }
  
  @Test
  void updateCourseShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).editFromRequest(getEditRequest());
    
    mockMvc.perform(put("/admin/courses/edit/id").with(csrf()).flashAttr("course", getEditRequest())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editFromRequest(getEditRequest());
  }
  
  @Test
  void getCreateCourseViewShouldReturnCourseCreateView() throws Exception {
    mockMvc.perform(get("/admin/courses/create")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Create Course</title>")));
  }
  
  @Test
  void getCreateCourseViewShouldAddExpectedAttributesToModel() throws Exception {
    mockMvc.perform(get("/admin/courses/create")).andExpect(status().isOk())
      .andExpect(model().attribute("course", CourseCreateRequest.builder().build()));
  }
  
  @Test
  void createCourseShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.saveFromRequest(getCreateRequest())).thenReturn(Course.builder()
        .id("id")
        .name("name")
        .description("description").build());
    when(service.findByName(any())).thenReturn(Course.builder()
        .id("id")
        .name("name")
        .description("description").build());
    
    mockMvc.perform(post("/admin/courses/create").with(csrf()).flashAttr("course", getCreateRequest())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getCreateRequest());
  }

  private CourseResponse getResponse() {
    return CourseResponse.builder()
        .id("course-id")
        .name("course-name")
        .description("course-description").build();
  }

  private List<CourseResponse> getResponses() {
    List<CourseResponse> responses = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responses.add(getResponse());
    }
    return responses;
  }
  
  private CourseEditRequest getEditRequest() {
    return CourseEditRequest.builder()
        .id("id")
        .name("name")
        .description("description").build();
  }
  
  private CourseCreateRequest getCreateRequest() {
    return CourseCreateRequest.builder()
        .name("course")
        .description("description").build();
  }
}
