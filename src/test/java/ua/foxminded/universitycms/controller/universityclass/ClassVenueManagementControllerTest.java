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
import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;
import ua.foxminded.universitycms.service.UniversityClassVenueService;

@WebMvcTest(ClassVenueManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
public class ClassVenueManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UniversityClassVenueService service;

  @Test
  void searchVenuesShouldReturnExpectedResults() throws Exception {
    String keyword = "hall";
    int fastSearchLimit = 5;
    List<UniversityClassVenueResponse> expectedResponses = List.of(
        UniversityClassVenueResponse.builder().id("1").name("Main Hall").build(),
        UniversityClassVenueResponse.builder().id("2").name("Science Hall").build());

    when(service.getClassVenueResponses(keyword, fastSearchLimit, 0)).thenReturn(new PageImpl<>(expectedResponses));

    mockMvc.perform(get("/admin/classes/venues/search")
        .param("keyword", keyword).param("fastSearchLimit", String.valueOf(fastSearchLimit)))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":\"1\",\"name\":\"Main Hall\"},{\"id\":\"2\",\"name\":\"Science Hall\"}]"));

    verify(service).getClassVenueResponses(keyword, fastSearchLimit, 0);
  }

  @Test
  void getVenuesShouldReturnVenueManagementView() throws Exception {
    when(service.getClassVenueResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<>(getResponses()));
    mockMvc.perform(get("/admin/classes/venues"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Venues</title>")));
  }

  @Test
  void getVenuesShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getClassVenueResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
    mockMvc.perform(get("/admin/classes/venues?keyword=keyword&pageNumber=1"))
        .andExpect(status().isOk());

    verify(service, atLeastOnce()).getClassVenueResponses("keyword", 5, 0);
  }

  @Test
  void getVenuesShouldDefaultToPageOneWhenPageNumberIsString() throws Exception {
      when(service.getClassVenueResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/classes/venues?keyword=keyword&pageNumber=invalid"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getClassVenueResponses("keyword", 5, 0);
  }

  @Test
  void getVenuesShouldDefaultToPageOneWhenPageNumberIsNegative() throws Exception {
      when(service.getClassVenueResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/classes/venues?keyword=keyword&pageNumber=-1"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getClassVenueResponses("keyword", 5, 0);
  }

  @Test
  void getVenuesShouldDefaultToPageOneWhenPageNumberIsZero() throws Exception {
      when(service.getClassVenueResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));
      mockMvc.perform(get("/admin/classes/venues?keyword=keyword&pageNumber=0"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));
      verify(service, atLeastOnce()).getClassVenueResponses("keyword", 5, 0);
  }

  @Test
  void getVenuesShouldAddExpectedAttributesToModel() throws Exception {
    Page<UniversityClassVenueResponse> pages = new PageImpl<>(getResponses());
    when(service.getClassVenueResponses("keyword", 5, 0)).thenReturn(pages);

    mockMvc.perform(get("/admin/classes/venues?keyword=keyword&pageNumber=1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("venues", pages.getContent()))
        .andExpect(model().attribute("currentPage", pages.getNumber() + 1))
        .andExpect(model().attribute("totalPages", pages.getTotalPages()))
        .andExpect(model().attribute("keyword", "keyword"));
  }

  @Test
  void deleteVenueShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).deleteById("id");

    mockMvc.perform(delete("/admin/classes/venues/id").with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).deleteById("id");
  }

  @Test
  void getEditVenueViewShouldReturnVenueEditView() throws Exception {
    when(service.getClassVenueEditRequestById("id")).thenReturn(getEditRequest());
    mockMvc.perform(get("/admin/classes/venues/edit/id"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Edit Venue</title>")));
  }

  @Test
  void getEditVenueViewShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getClassVenueEditRequestById("id")).thenReturn(getEditRequest());

    mockMvc.perform(get("/admin/classes/venues/edit/id"))
        .andExpect(status().isOk());

    verify(service, atLeastOnce()).getClassVenueEditRequestById("id");
  }

  @Test
  void getEditVenueViewShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getClassVenueEditRequestById("id")).thenReturn(getEditRequest());

    mockMvc.perform(get("/admin/classes/venues/edit/id"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("venue", getEditRequest()));
  }

  @Test
  void updateVenueShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).editFromRequest(getEditRequest());

    mockMvc.perform(put("/admin/classes/venues/edit/id").with(csrf()).flashAttr("venue", getEditRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editFromRequest(getEditRequest());
  }

  @Test
  void getCreateVenueViewShouldReturnVenueCreateView() throws Exception {
    mockMvc.perform(get("/admin/classes/venues/create"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Create Venue</title>")));
  }

  @Test
  void getCreateVenueViewShouldAddExpectedAttributesToModel() throws Exception {
    mockMvc.perform(get("/admin/classes/venues/create"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("venue", UniversityClassVenueCreateRequest.builder().build()));
  }

  @Test
  void createVenueShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.saveFromRequest(getCreateRequest())).thenReturn(UniversityClassVenue.builder()
        .id("id")
        .name("name").build());
    when(service.findByName(any())).thenReturn(UniversityClassVenue.builder()
        .id("id")
        .name("name").build());

    mockMvc.perform(post("/admin/classes/venues/create").with(csrf()).flashAttr("venue", getCreateRequest()))
        .andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getCreateRequest());
  }

  private UniversityClassVenueResponse getResponse() {
    return UniversityClassVenueResponse.builder()
        .id("venue-id")
        .name("venue-name").build();
  }

  private List<UniversityClassVenueResponse> getResponses() {
    List<UniversityClassVenueResponse> responses = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responses.add(getResponse());
    }
    return responses;
  }

  private UniversityClassVenueEditRequest getEditRequest() {
    return UniversityClassVenueEditRequest.builder()
        .id("id")
        .name("name").build();
  }

  private UniversityClassVenueCreateRequest getCreateRequest() {
    return UniversityClassVenueCreateRequest.builder()
        .name("venue").build();
  }
}
