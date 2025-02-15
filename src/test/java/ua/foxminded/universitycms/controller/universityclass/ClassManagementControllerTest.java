package ua.foxminded.universitycms.controller.universityclass;

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
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;
import ua.foxminded.universitycms.service.UniversityClassService;
import ua.foxminded.universitycms.service.UniversityClassTypeService;

@WebMvcTest(ClassManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
public class ClassManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UniversityClassService service;
  @MockBean
  private UniversityClassTypeService typeService;

  @Test
  void getUniversityClassesShouldReturnClassManagementView() throws Exception {
    when(service.getUniversityClassResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<>(getUniversityClassResponses()));
    mockMvc.perform(get("/admin/classes"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Classes</title>")));
  }

  @Test
  void getUniversityClassesShouldDefaultToPageOneWhenPageNumberIsString() throws Exception {
    Page<UniversityClassResponse> pages = new PageImpl<>(getUniversityClassResponses());
    when(service.getUniversityClassResponses("keyword", 5, "invalid")).thenReturn(pages);

    mockMvc.perform(get("/admin/classes?keyword=keyword&pageNumber=invalid"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("currentPage", 1));

    verify(service, atLeastOnce()).getUniversityClassResponses("keyword", 5, "invalid");
  }

  @Test
  void getUniversityClassesShouldDefaultToPageOneWhenPageNumberIsNegative() throws Exception {
    Page<UniversityClassResponse> pages = new PageImpl<>(getUniversityClassResponses());
    when(service.getUniversityClassResponses("keyword", 5, "-1")).thenReturn(pages);

    mockMvc.perform(get("/admin/classes?keyword=keyword&pageNumber=-1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("currentPage", 1));

    verify(service, atLeastOnce()).getUniversityClassResponses("keyword", 5, "-1");
  }

  @Test
  void getUniversityClassesShouldDefaultToPageOneWhenPageNumberIsZero() throws Exception {
    Page<UniversityClassResponse> pages = new PageImpl<>(getUniversityClassResponses());
    when(service.getUniversityClassResponses("keyword", 5, "0")).thenReturn(pages);

    mockMvc.perform(get("/admin/classes?keyword=keyword&pageNumber=0"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("currentPage", 1));

    verify(service, atLeastOnce()).getUniversityClassResponses("keyword", 5, "0");
  }

  @Test
  void getUniversityClassesShouldAddExpectedAttributesToModel() throws Exception {
    Page<UniversityClassResponse> pages = new PageImpl<>(getUniversityClassResponses());
    when(service.getUniversityClassResponses("keyword", 5, "1")).thenReturn(pages);

    mockMvc.perform(get("/admin/classes?keyword=keyword&pageNumber=1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("classes", pages.getContent()))
        .andExpect(model().attribute("currentPage", pages.getNumber() + 1))
        .andExpect(model().attribute("totalPages", pages.getTotalPages()))
        .andExpect(model().attribute("keyword", "keyword"));
  }

  @Test
  void getUniversityClassShouldReturnClassView() throws Exception {
    when(service.getUniversityClassResponseById("id")).thenReturn(getUniversityClassResponse());
    mockMvc.perform(get("/admin/classes/id"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Class</title>")));
  }

  @Test
  void getUniversityClassShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getUniversityClassResponseById("id")).thenReturn(getUniversityClassResponse());

    mockMvc.perform(get("/admin/classes/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getUniversityClassResponseById("id");
  }

  @Test
  void getUniversityClassShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getUniversityClassResponseById("id")).thenReturn(getUniversityClassResponse());

    mockMvc.perform(get("/admin/classes/id")).andExpect(status().isOk())
        .andExpect(model().attribute("class", getUniversityClassResponse()));
  }

  @Test
  void deleteUniversityClassShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).deleteById("id");

    mockMvc.perform(delete("/admin/classes/id").with(csrf())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).deleteById("id");
  }

  @Test
  void getEditUniversityClassViewShouldReturnClassEditView() throws Exception {
    when(service.getUniversityClassEditRequestById("id")).thenReturn(getUniversityClassEditRequest());
    when(typeService.getUniversityClassTypeResponses()).thenReturn(getUniversityClassTypeResponses());
    mockMvc.perform(get("/admin/classes/edit/id"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Edit Class</title>")));
  }

  @Test
  void getEditUniversityClassViewShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getUniversityClassEditRequestById("id")).thenReturn(getUniversityClassEditRequest());

    mockMvc.perform(get("/admin/classes/edit/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getUniversityClassEditRequestById("id");
  }

  @Test
  void getEditUniversityClassViewShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getUniversityClassEditRequestById("id")).thenReturn(getUniversityClassEditRequest());
    when(typeService.getUniversityClassTypeResponses()).thenReturn(getUniversityClassTypeResponses());

    mockMvc.perform(get("/admin/classes/edit/id")).andExpect(status().isOk())
        .andExpect(model().attribute("class", getUniversityClassEditRequest()))
        .andExpect(model().attribute("types", getUniversityClassTypeResponses()));
  }

  @Test
  void updateUniversityClassShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).editFromRequest(getUniversityClassEditRequest());

    mockMvc.perform(put("/admin/classes/edit/id").with(csrf()).flashAttr("class", getUniversityClassEditRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editFromRequest(getUniversityClassEditRequest());
  }

  @Test
  void getCreateUniversityClassViewShouldReturnClassCreateView() throws Exception {
    when(typeService.getUniversityClassTypeResponses()).thenReturn(getUniversityClassTypeResponses());

    mockMvc.perform(get("/admin/classes/create")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Create Class</title>")));
  }

  @Test
  void getCreateUniversityClassViewShouldAddExpectedAttributesToModel() throws Exception {
    when(typeService.getUniversityClassTypeResponses()).thenReturn(getUniversityClassTypeResponses());

    mockMvc.perform(get("/admin/classes/create")).andExpect(status().isOk())
        .andExpect(model().attribute("class", UniversityClassCreateRequest.builder().build()))
        .andExpect(model().attribute("types", getUniversityClassTypeResponses()));
  }

  @Test
  void createUniversityClassShouldCallService() throws Exception {
    when(service.saveFromRequest(getUniversityClassCreateRequest())).thenReturn(UniversityClass.builder().build());

    mockMvc.perform(post("/admin/classes/create").with(csrf()).flashAttr("class", getUniversityClassCreateRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getUniversityClassCreateRequest());
  }

  @Test
  void createUniversityClassesWithRepeatOptionShouldCallService() throws Exception {
    when(service.saveFromRequest(any(UniversityClassCreateRequest.class), anyString(),
        anyString())).thenReturn(List.of(UniversityClass.builder().build()));

    mockMvc
        .perform(post("/admin/classes/create/batch?repeat=daily&repeatUntil=2025-01-05").with(csrf()).flashAttr("class",
            getUniversityClassCreateRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getUniversityClassCreateRequest(), "daily", "2025-01-05");
  }

  private UniversityClassResponse getUniversityClassResponse() {
    return UniversityClassResponse.builder()
        .id("id")
        .type("Lecture")
        .venue("Room 101")
        .course("Computer Science")
        .groupNames(List.of("Group A"))
        .teacherLogins(List.of("Dr. Smith"))
        .dateTime("2025-01-05T14:30:00+05:30")
        .build();
  }

  private List<UniversityClassResponse> getUniversityClassResponses() {
    List<UniversityClassResponse> responses = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responses.add(getUniversityClassResponse());
    }
    return responses;
  }

  private UniversityClassEditRequest getUniversityClassEditRequest() {
    return UniversityClassEditRequest.builder()
        .id("id")
        .type("Lecture")
        .venue("Room 101")
        .course("Computer Science")
        .groupNames(List.of("Group A"))
        .teacherLogins(List.of("Dr. Smith"))
        .dateTime("2025-01-05T14:30:00+05:30")
        .build();
  }

  private UniversityClassCreateRequest getUniversityClassCreateRequest() {
    return UniversityClassCreateRequest.builder()
        .type("Lecture")
        .venue("Room 101")
        .course("Computer Science")
        .groupNames(List.of("Group A"))
        .teacherLogins(List.of("Dr. Smith"))
        .dateTime("2025-01-05T14:30:00+05:30")
        .build();
  }

  private List<UniversityClassTypeResponse> getUniversityClassTypeResponses() {
    return List.of(UniversityClassTypeResponse.builder().name("Lecture").build(),
        UniversityClassTypeResponse.builder().name("Seminr").build(),
        UniversityClassTypeResponse.builder().name("Lab").build());
  }
}
