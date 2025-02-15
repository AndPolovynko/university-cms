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

import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;
import ua.foxminded.universitycms.mapper.UniversityClassVenueMapper;
import ua.foxminded.universitycms.repository.UniversityClassVenueRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class UniversityClassVenueServiceImplTest {
  @Mock
  UniversityClassVenueRepository repo;

  @Mock
  UniversityClassVenueMapper mapper;

  @InjectMocks
  UniversityClassVenueServiceImpl service;

  @Captor
  ArgumentCaptor<UniversityClassVenue> captor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.classVenueCreateRequestToClassVenue(any(UniversityClassVenueCreateRequest.class)))
        .thenReturn(getEntity());

    UniversityClassVenueCreateRequest request = UniversityClassVenueCreateRequest.builder()
        .name("name").build();

    service.saveFromRequest(request);

    verify(mapper, atLeastOnce()).classVenueCreateRequestToClassVenue(request);
  }

  @Test
  void getClassVenueResponseByIdShouldCallMapper() {
    UniversityClassVenueResponse response = UniversityClassVenueResponse.builder()
        .name("name").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.classVenueToClassVenueResponse(any(UniversityClassVenue.class))).thenReturn(response);

    service.getClassVenueResponseById("id");

    verify(mapper, atLeastOnce()).classVenueToClassVenueResponse(any(UniversityClassVenue.class));
  }

  @Test
  void getClassVenueResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getClassVenueResponseById("test-id"));

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

    assertThat(service.findByName("name")).isEqualTo(getEntity());
  }

  @Test
  void getClassVenueEditRequestByIdShouldCallMapper() {
    UniversityClassVenueEditRequest request = UniversityClassVenueEditRequest.builder()
        .id("")
        .name("name").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.classVenueToClassVenueEditRequest(any(UniversityClassVenue.class))).thenReturn(request);

    service.getClassVenueEditRequestById("id");

    verify(mapper, atLeastOnce()).classVenueToClassVenueEditRequest(getEntity());
  }

  @Test
  void getClassVenueEditRequestByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getClassVenueEditRequestById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void getClassVenueResponsesShouldCallMapperIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(getEntities()));

    service.getClassVenueResponses(null, 1, "0");

    verify(mapper, atLeastOnce()).classVenueToClassVenueResponse(any());
  }

  @Test
  void getClassVenueResponsesShouldCallRepoFindAllIfKeywordIsNull() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(getEntities()));

    service.getClassVenueResponses(null, 1, "0");

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getClassVenueResponsesShouldCallRepoFindAllIfKeywordIsBlank() {
    when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(getEntities()));

    service.getClassVenueResponses("", 1, "0");

    verify(repo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getClassVenueResponsesShouldCallMapperIfKeywordIsNotNull() {
    when(repo.findByNameContaining(any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(getEntities()));

    service.getClassVenueResponses("keyword", 1, "1");

    verify(mapper, atLeastOnce()).classVenueToClassVenueResponse(any());
  }

  @Test
  void getClassVenueResponsesShouldCallRepoFindByNameContainingIfKeywordIsNotNull() {
    when(repo.findByNameContaining(any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(getEntities()));

    service.getClassVenueResponses("keyword", 1, "-1");

    verify(repo, atLeastOnce()).findByNameContaining(any(), any(PageRequest.class));
  }

  @Test
  void editFromRequestShouldCallMapper() {
    when(mapper.classVenueEditRequestToClassVenue(any(UniversityClassVenueEditRequest.class))).thenReturn(getEntity());

    UniversityClassVenueEditRequest request = UniversityClassVenueEditRequest.builder()
        .id("id")
        .name("name").build();

    service.editFromRequest(request);

    verify(mapper, atLeastOnce()).classVenueEditRequestToClassVenue(request);
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(repo).deleteById(any());

    service.deleteById("id");

    verify(repo, atLeastOnce()).deleteById("id");
  }

  private UniversityClassVenue getEntity() {
    return UniversityClassVenue.builder().build();
  }

  private List<UniversityClassVenue> getEntities() {
    List<UniversityClassVenue> entities = new ArrayList<>();
    entities.add(UniversityClassVenue.builder().id("1").build());
    entities.add(UniversityClassVenue.builder().id("2").build());

    return entities;
  }
}
