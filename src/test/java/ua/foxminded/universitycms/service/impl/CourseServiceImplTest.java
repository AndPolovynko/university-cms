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

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;
import ua.foxminded.universitycms.mapper.CourseMapper;
import ua.foxminded.universitycms.repository.CourseRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
  @Mock
  CourseRepository repo;
  @Mock
  CourseMapper mapper;

  @InjectMocks
  CourseServiceImpl service;

  @Captor
  ArgumentCaptor<Course> captor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.courseCreateRequestToCourse(any(CourseCreateRequest.class))).thenReturn(getEntity());

    CourseCreateRequest request = CourseCreateRequest.builder()
        .name("name")
        .description("description").build();

    service.saveFromRequest(request);

    verify(mapper, atLeastOnce()).courseCreateRequestToCourse(request);
  }

  @Test
  void getCourseResponseByIdShouldCallMapper() {
    CourseResponse response = CourseResponse.builder()
        .name("name")
        .description("description").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.courseToCourseResponse(any(Course.class))).thenReturn(response);

    service.getCourseResponseById("id");

    verify(mapper, atLeastOnce()).courseToCourseResponse(any(Course.class));
  }

  @Test
  void getCourseResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getCourseResponseById("test-id"));

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
  void getCourseEditRequestByIdShouldCallMapper() {
    CourseEditRequest request = CourseEditRequest.builder()
        .id("")
        .name("name")
        .description("description").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.courseToCourseEditRequest(any(Course.class))).thenReturn(request);

    service.getCourseEditRequestById("id");

    verify(mapper, atLeastOnce()).courseToCourseEditRequest(getEntity());
  }

  @Test
  void getCourseEditRequestByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenNameDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getCourseEditRequestById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void getCourseResponsesShouldCallMapperIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Course>(getEntities()));

    service.getCourseResponses(null, 1, "0");

    verify(mapper, atLeastOnce()).courseToCourseResponse(any());
  }

  @Test
  void getCourseResponsesShouldCallRepoFindAllIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Course>(getEntities()));

    service.getCourseResponses(null, 1, "1");

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }
  
  @Test
  void getCourseResponsesShouldCallRepoFindAllIfKeywordIsBlank() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<Course>(getEntities()));

    service.getCourseResponses("", 1, "-1");

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getCourseResponsesShouldCallMapperIfKeywordIsNotNull() {
    when(repo.findByNameContainingOrDescriptionContaining(any(), any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<Course>(getEntities()));

    service.getCourseResponses("keyword", 1, "0");

    verify(mapper, atLeastOnce()).courseToCourseResponse(any());
  }

  @Test
  void getCourseResponsesShouldCallRepoFindByNameContainingOrDescriptionContainingIfKeywordIsNotNull() {
    when(repo.findByNameContainingOrDescriptionContaining(any(), any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<Course>(getEntities()));

    service.getCourseResponses("keyword", 1, "0");

    verify(repo, atLeastOnce()).findByNameContainingOrDescriptionContaining(any(), any(), any(PageRequest.class));
  }

  @Test
  void editFromRequestShouldCallMapper() {
    when(mapper.courseEditRequestToCourse(any(CourseEditRequest.class))).thenReturn(getEntity());

    CourseEditRequest request = CourseEditRequest.builder()
        .id("id")
        .name("name")
        .description("description").build();

    service.editFromRequest(request);

    verify(mapper, atLeastOnce()).courseEditRequestToCourse(request);
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(repo).deleteById(any());

    service.deleteById("id");

    verify(repo, atLeastOnce()).deleteById("id");
  }

  private Course getEntity() {
    return Course.builder().build();
  }

  private List<Course> getEntities() {
    List<Course> entities = new ArrayList<>();
    entities.add(Course.builder().id("1").build());
    entities.add(Course.builder().id("2").build());

    return entities;
  }
}
