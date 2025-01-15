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
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;
import ua.foxminded.universitycms.service.GroupService;

@WebMvcTest(GroupManagementController.class)
@Import(SpringSecurityConfig.class)
@WithMockUser(roles = "ADMINISTRATOR")
class GroupManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GroupService service;
  
  @Test
  void searchGroupsShouldReturnExpetcedPage() throws Exception {
    when(service.getGroupResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<GroupResponse>(getResponses()));
    mockMvc.perform(get("/admin/groups/search")).andExpect(status().isOk())
        .andExpect(content().string(containsString("group-name")));
  }

  @Test
  void getGroupsShouldReturnGroupManagementView() throws Exception {
    when(service.getGroupResponses(anyString(), any(), any()))
        .thenReturn(new PageImpl<GroupResponse>(getResponses()));
    mockMvc.perform(get("/admin/groups")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Groups</title>")));
  }

  @Test
  void getGroupsShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getGroupResponses("keyword", 5, 0)).thenReturn(new PageImpl<GroupResponse>(getResponses()));
    mockMvc.perform(get("/admin/groups?keyword=keyword&pageNumber=1")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getGroupResponses("keyword", 5, 0);
  }
  
  @Test
  void getGroupsShouldAddExpectedAttributesToModel() throws Exception {
    Page<GroupResponse> pages = new PageImpl<>(getResponses());
    when(service.getGroupResponses("keyword", 5, 0)).thenReturn(pages);
    
    mockMvc.perform(get("/admin/groups?keyword=keyword&pageNumber=1"))
      .andExpect(status().isOk())
      .andExpect(model().attribute("groups", pages.getContent()))
      .andExpect(model().attribute("currentPage", pages.getNumber() + 1))
      .andExpect(model().attribute("totalPages", pages.getTotalPages()))
      .andExpect(model().attribute("keyword", "keyword"));
  }
  
  @Test
  void getGroupsShouldDefaultToPageOneWhenPageNumberIsString() throws Exception {
      when(service.getGroupResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/groups?keyword=keyword&pageNumber=invalid"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getGroupResponses("keyword", 5, 0);
  }
  
  @Test
  void getGroupsShouldDefaultToPageOneWhenPageNumberIsNegative() throws Exception {
      when(service.getGroupResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/groups?keyword=keyword&pageNumber=-1"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getGroupResponses("keyword", 5, 0);
  }
  
  @Test
  void getGroupsShouldDefaultToPageOneWhenPageNumberIsZero() throws Exception {
      when(service.getGroupResponses("keyword", 5, 0)).thenReturn(new PageImpl<>(getResponses()));

      mockMvc.perform(get("/admin/groups?keyword=keyword&pageNumber=0"))
          .andExpect(status().isOk())
          .andExpect(model().attribute("currentPage", 1));

      verify(service, atLeastOnce()).getGroupResponses("keyword", 5, 0);
  }
  
  @Test
  void getGroupShouldReturnGroupView() throws Exception {
    when(service.getGroupResponseById("id")).thenReturn(getResponse());
    mockMvc.perform(get("/admin/groups/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Group</title>")));
  }
  
  @Test
  void getGroupShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getGroupResponseById("id")).thenReturn(getResponse());
    
    mockMvc.perform(get("/admin/groups/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getGroupResponseById("id");
  }
  
  @Test
  void getGroupShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getGroupResponseById("id")).thenReturn(getResponse());
    
    mockMvc.perform(get("/admin/groups/id")).andExpect(status().isOk())
      .andExpect(model().attribute("group", getResponse()));
  }
  
  @Test
  void deleteGroupShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).deleteById("id");
    
    mockMvc.perform(delete("/admin/groups/id").with(csrf())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).deleteById("id");
  }
  
  @Test
  void getEditGroupViewShouldReturnGroupEditView() throws Exception {
    when(service.getGroupEditRequestById("id")).thenReturn(getEditRequest());
    mockMvc.perform(get("/admin/groups/edit/id")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Edit Group</title>")));
  }
  
  @Test
  void getEditGroupViewShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.getGroupEditRequestById("id")).thenReturn(getEditRequest());
    
    mockMvc.perform(get("/admin/groups/edit/id")).andExpect(status().isOk());

    verify(service, atLeastOnce()).getGroupEditRequestById("id");
  }
  
  @Test
  void getEditGroupViewShouldAddExpectedAttributesToModel() throws Exception {
    when(service.getGroupEditRequestById("id")).thenReturn(getEditRequest());
    
    mockMvc.perform(get("/admin/groups/edit/id")).andExpect(status().isOk())
      .andExpect(model().attribute("group", getEditRequest()));
  }
  
  @Test
  void updateGroupShouldCallServiceMethodWithExpectedArguments() throws Exception {
    doNothing().when(service).editFromRequest(getEditRequest());
    
    mockMvc.perform(put("/admin/groups/edit/id").with(csrf()).flashAttr("group", getEditRequest())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).editFromRequest(getEditRequest());
  }
  
  @Test
  void getCreateGroupViewShouldReturnGroupCreateView() throws Exception {
    mockMvc.perform(get("/admin/groups/create")).andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Create Group</title>")));
  }
  
  @Test
  void getCreateGroupViewShouldAddExpectedAttributesToModel() throws Exception {
    mockMvc.perform(get("/admin/groups/create")).andExpect(status().isOk())
      .andExpect(model().attribute("group", GroupCreateRequest.builder().build()));
  }
  
  @Test
  void createGroupShouldCallServiceMethodWithExpectedArguments() throws Exception {
    when(service.saveFromRequest(getCreateRequest())).thenReturn(Group.builder()
        .id("id")
        .name("name").build());
    when(service.findByName(any())).thenReturn(Group.builder()
        .id("id")
        .name("name").build());
    
    mockMvc.perform(post("/admin/groups/create").with(csrf()).flashAttr("group", getCreateRequest())).andExpect(status().is3xxRedirection());

    verify(service, atLeastOnce()).saveFromRequest(getCreateRequest());
  }

  private GroupResponse getResponse() {
    return GroupResponse.builder()
        .id("group-id")
        .name("group-name").build();
  }

  private List<GroupResponse> getResponses() {
    List<GroupResponse> responses = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      responses.add(getResponse());
    }
    return responses;
  }
  
  private GroupEditRequest getEditRequest() {
    return GroupEditRequest.builder()
        .id("id")
        .name("name").build();
  }
  
  private GroupCreateRequest getCreateRequest() {
    return GroupCreateRequest.builder()
        .name("group").build();
  }
}
