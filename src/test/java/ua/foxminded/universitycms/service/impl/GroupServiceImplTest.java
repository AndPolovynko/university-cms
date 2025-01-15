package ua.foxminded.universitycms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;
import ua.foxminded.universitycms.mapper.GroupMapper;
import ua.foxminded.universitycms.repository.GroupRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {
  @Mock
  GroupRepository repo;
  @Mock
  GroupMapper mapper;

  @InjectMocks
  GroupServiceImpl service;

  @Captor
  ArgumentCaptor<Group> captor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.groupCreateRequestToGroup(any(GroupCreateRequest.class))).thenReturn(getEntity());

    GroupCreateRequest request = GroupCreateRequest.builder()
        .name("name").build();

    service.saveFromRequest(request);

    verify(mapper, atLeastOnce()).groupCreateRequestToGroup(request);
  }

  @Test
  void getGroupResponseByIdShouldCallMapper() {
    GroupResponse response = GroupResponse.builder()
        .name("name").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.groupToGroupResponse(any(Group.class))).thenReturn(response);

    service.getGroupResponseById("id");

    verify(mapper, atLeastOnce()).groupToGroupResponse(any(Group.class));
  }

  @Test
  void getGroupResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getGroupResponseById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void findByNameShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenNameDoesNotExist() {
    when(repo.findByName(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.findByName("test-name"));

    assertThat(exception.getMessage()).contains("test-name");
  }

  @Test
  void findByNameShouldCallRepositoryMethod() {
    when(repo.findByName(any())).thenReturn(Optional.of(getEntity()));

    service.findByName("name");

    verify(repo, atLeastOnce()).findByName("name");
  }

  @Test
  void findByNameShouldReturnRepositoryMethodOutput() {
    when(repo.findByName(any())).thenReturn(Optional.of(getEntity()));

    assertThat(service.findByName("name").equals(getEntity()));
  }

  @Test
  void getGroupEditRequestByIdShouldCallMapper() {
    GroupEditRequest request = GroupEditRequest.builder()
        .id("")
        .name("name").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.groupToGroupEditRequest(any(Group.class))).thenReturn(request);

    service.getGroupEditRequestById("id");

    verify(mapper, atLeastOnce()).groupToGroupEditRequest(getEntity());
  }

  @Test
  void getGroupEditRequestByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenNameDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getGroupEditRequestById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void getGroupResponsesShouldCallMapperIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Group>(getEntities()));

    service.getGroupResponses(null, 1, 0);

    verify(mapper, atLeastOnce()).groupToGroupResponse(any());
  }

  @Test
  void getGroupResponsesShouldCallRepoFindAllIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Group>(getEntities()));

    service.getGroupResponses(null, 1, 0);

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }
  
  @Test
  void getGroupResponsesShouldCallRepoFindAllIfKeywordIsBlank() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Group>(getEntities()));

    service.getGroupResponses("", 1, 0);

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getGroupResponsesShouldCallMapperIfKeywordIsNotNull() {
    when(repo.findByNameContaining(any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<Group>(getEntities()));

    service.getGroupResponses("keyword", 1, 0);

    verify(mapper, atLeastOnce()).groupToGroupResponse(any());
  }

  @Test
  void getGroupResponsesShouldCallRepoFindByNameContainingOrDescriptionContainingIfKeywordIsNotNull() {
    when(repo.findByNameContaining(any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<Group>(getEntities()));

    service.getGroupResponses("keyword", 1, 0);

    verify(repo, atLeastOnce()).findByNameContaining(any(), any(PageRequest.class));
  }

  @Test
  void editFromRequestShouldCallMapper() {
    when(mapper.groupEditRequestToGroup(any(GroupEditRequest.class))).thenReturn(getEntity());

    GroupEditRequest request = GroupEditRequest.builder()
        .id("id")
        .name("name").build();

    service.editFromRequest(request);

    verify(mapper, atLeastOnce()).groupEditRequestToGroup(request);
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(repo).deleteById(any());

    service.deleteById("id");

    verify(repo, atLeastOnce()).deleteById("id");
  }

  private Group getEntity() {
    return Group.builder().build();
  }

  private List<Group> getEntities() {
    List<Group> entities = new ArrayList<>();
    entities.add(Group.builder().id("1").build());
    entities.add(Group.builder().id("2").build());

    return entities;
  }
}
