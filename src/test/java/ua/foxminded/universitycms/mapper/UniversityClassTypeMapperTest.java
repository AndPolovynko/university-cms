package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;

class UniversityClassTypeMapperTest {
  UniversityClassTypeMapper mapper = new UniversityClassTypeMapperImpl();

  @Test
  void classTypeToClassTypeResponseShouldReturnNullIfUniversityClassTypeIsNull() {
    UniversityClassType universityClassType = null;
    assertThat(mapper.classTypeToClassTypeResponse(universityClassType)).isEqualTo(null);
  }

  @Test
  void classTypeToClassTypeResponseShouldReturnExpectedUniversityClassTypeResponse() {
    UniversityClassType universityClassType = getUniversityClassType();

    UniversityClassTypeResponse expectedResponse = UniversityClassTypeResponse.builder()
        .id("id")
        .name("name").build();

    assertThat(mapper.classTypeToClassTypeResponse(universityClassType)).isEqualTo(expectedResponse);
  }

  @Test
  void classTypeCreateRequestToClassTypeShouldReturnNullIfRequestIsNull() {
    UniversityClassTypeCreateRequest request = null;
    assertThat(mapper.classTypeCreateRequestToClassType(request)).isEqualTo(null);
  }

  @Test
  void classTypeCreateRequestToClassTypeShouldReturnExpectedUniversityClassType() {
    UniversityClassTypeCreateRequest request = UniversityClassTypeCreateRequest.builder()
        .name("name").build();

    UniversityClassType expectedClassType = getUniversityClassType();
    expectedClassType.setId(null);

    assertThat(mapper.classTypeCreateRequestToClassType(request)).isEqualTo(expectedClassType);
  }

  private UniversityClassType getUniversityClassType() {
    return UniversityClassType.builder()
        .id("id")
        .name("name").build();
  }
}
