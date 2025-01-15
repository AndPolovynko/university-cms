package ua.foxminded.universitycms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;
import ua.foxminded.universitycms.mapper.UniversityClassTypeMapper;
import ua.foxminded.universitycms.repository.UniversityClassTypeRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class UniversityClassTypeServiceImplTest {
  @Mock
  UniversityClassTypeRepository repo;
  @Mock
  UniversityClassTypeMapper mapper;

  @InjectMocks
  UniversityClassTypeServiceImpl service;

  @Captor
  ArgumentCaptor<UniversityClassType> captor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.classTypeCreateRequestToClassType(any(UniversityClassTypeCreateRequest.class)))
        .thenReturn(getEntity());

    UniversityClassTypeCreateRequest request = UniversityClassTypeCreateRequest.builder()
        .name("name").build();

    service.saveFromRequest(request);

    verify(mapper, atLeastOnce()).classTypeCreateRequestToClassType(request);
  }

  @Test
  void getUniversityClassTypeResponseByIdShouldCallMapper() {
    UniversityClassTypeResponse response = UniversityClassTypeResponse.builder()
        .name("name").build();

    when(repo.findById(any())).thenReturn(Optional.of(getEntity()));
    when(mapper.classTypeToClassTypeResponse(any(UniversityClassType.class))).thenReturn(response);

    service.getUniversityClassTypeResponseById("id");

    verify(mapper, atLeastOnce()).classTypeToClassTypeResponse(any(UniversityClassType.class));
  }

  @Test
  void getUniversityClassTypeResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(repo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUniversityClassTypeResponseById("test-id"));

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
  void getUniversityClassTypeResponsesShouldReturnMappedResponses() {
    List<UniversityClassType> entities = getEntities();

    when(repo.findAll()).thenReturn(entities);
    when(mapper.classTypeToClassTypeResponse(any(UniversityClassType.class)))
        .thenReturn(UniversityClassTypeResponse.builder().name("name").build());

    List<UniversityClassTypeResponse> result = service.getUniversityClassTypeResponses();

    assertThat(result).isEqualTo(List.of(UniversityClassTypeResponse.builder().name("name").build(),
        UniversityClassTypeResponse.builder().name("name").build()));
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(repo).deleteById(any());

    service.deleteById("id");

    verify(repo, atLeastOnce()).deleteById("id");
  }

  private UniversityClassType getEntity() {
    return UniversityClassType.builder().id("id").name("name").build();
  }

  private List<UniversityClassType> getEntities() {
    return List.of(getEntity(), getEntity());
  }
}
