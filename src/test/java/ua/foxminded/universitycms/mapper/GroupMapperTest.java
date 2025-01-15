package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;

class GroupMapperTest {
  GroupMapper mapper = new GroupMapperImpl();
  
  @Test
  void groupToGroupResponseShouldReturnNullIfGroupIsNull() {
    Group group = null;
    assertThat(mapper.groupToGroupResponse(group)).isEqualTo(null);
  }
  
  @Test
  void groupToGroupResponseShouldReturnExpectedGroupResponse() {
    Group group = getGroup();
    
    GroupResponse expectedResponse = GroupResponse.builder()
        .id("id")
        .name("name").build();
        
    assertThat(mapper.groupToGroupResponse(group)).isEqualTo(expectedResponse);
  }
  
  @Test
  void groupToGroupEditRequestShouldReturnNullIfGroupIsNull() {
    Group group = null;
    assertThat(mapper.groupToGroupEditRequest(group)).isEqualTo(null);
  }
  
  @Test
  void groupToGroupEditRequestShouldReturnExpectedGroupEditRequest() {
    Group group = getGroup();
    
    GroupEditRequest expectedRequest = GroupEditRequest.builder()
        .id("id")
        .name("name").build();
        
    assertThat(mapper.groupToGroupEditRequest(group)).isEqualTo(expectedRequest);
  }
  
  @Test
  void groupCreateRequestToGroupShouldReturnNullIfGroupCreateRequestIsNull() {
    GroupCreateRequest request = null;
    assertThat(mapper.groupCreateRequestToGroup(request)).isEqualTo(null);
  }
  
  @Test
  void groupEditRequestToGroupShouldReturnNullIfGroupEditRequestToGroupIsNull() {
    GroupEditRequest group = null;
    assertThat(mapper.groupEditRequestToGroup(group)).isEqualTo(null);
  }
  
  @Test
  void groupCreateRequestToGroupShouldReturnExpectedGroup() {
    GroupCreateRequest request = GroupCreateRequest.builder()
        .name("name").build();
    
    Group expectedGroup = getGroup();
    expectedGroup.setId(null);
    
    assertThat(mapper.groupCreateRequestToGroup(request)).isEqualTo(expectedGroup);
  }
  
  @Test
  void groupEditRequestToGroupShouldReturnExpectedGroup() {
    GroupEditRequest request = GroupEditRequest.builder()
        .id("id")
        .name("name").build();
    
    Group expectedGroup = getGroup();
    
    assertThat(mapper.groupEditRequestToGroup(request)).isEqualTo(expectedGroup);
  }
  
  private Group getGroup() {
    return Group.builder()
        .id("id")
        .name("name").build();
  }
}
