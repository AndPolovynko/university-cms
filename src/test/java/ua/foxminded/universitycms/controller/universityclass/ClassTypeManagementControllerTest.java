package ua.foxminded.universitycms.controller.universityclass;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ua.foxminded.universitycms.SpringSecurityConfig;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;
import ua.foxminded.universitycms.service.UniversityClassTypeService;

@WebMvcTest(ClassTypeManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
public class ClassTypeManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;
  
  @MockBean
  private UniversityClassTypeService service;
  
  @Test
  void getUniversityClassTypesShouldReturnClassTypeManagementView() throws Exception {
      when(service.getUniversityClassTypeResponses()).thenReturn(new ArrayList<>());
      mockMvc.perform(get("/admin/classes/types"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("<title>Types</title>")));
  }

  @Test
  void getUniversityClassTypesShouldAddExpectedAttributesToModel() throws Exception {
      List<UniversityClassTypeResponse> types = new ArrayList<>();
      when(service.getUniversityClassTypeResponses()).thenReturn(types);

      mockMvc.perform(get("/admin/classes/types"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("types", types));
  }

  @Test
  void deleteUniversityClassTypeShouldCallServiceMethodWithExpectedArguments() throws Exception {
      doNothing().when(service).deleteById("id");

      mockMvc.perform(delete("/admin/classes/types/id").with(csrf()))
          .andExpect(status().is3xxRedirection());

      verify(service, atLeastOnce()).deleteById("id");
  }

  @Test
  void getCreateUniversityClassTypeViewShouldReturnCreateView() throws Exception {
      mockMvc.perform(get("/admin/classes/types/create"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("<title>Create Class Type</title>")));
  }

  @Test
  void getCreateUniversityClassTypeViewShouldAddExpectedAttributesToModel() throws Exception {
      mockMvc.perform(get("/admin/classes/types/create"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("type", UniversityClassTypeCreateRequest.builder().build()));
  }

  @Test
  void createUniversityClassTypeShouldCallServiceMethodWithExpectedArguments() throws Exception {
      UniversityClassTypeCreateRequest request = UniversityClassTypeCreateRequest.builder().build();

      mockMvc.perform(post("/admin/classes/types/create")
              .with(csrf())
              .flashAttr("class", request))
          .andExpect(status().is3xxRedirection());

      verify(service, atLeastOnce()).saveFromRequest(request);
  }
}
