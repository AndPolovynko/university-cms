package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;

class UniversityClassVenueMapperTest {
  UniversityClassVenueMapper mapper = new UniversityClassVenueMapperImpl();

  @Test
  void classVenueToClassVenueResponseShouldReturnNullIfVenueIsNull() {
      UniversityClassVenue venue = null;
      assertThat(mapper.classVenueToClassVenueResponse(venue)).isEqualTo(null);
  }

  @Test
  void classVenueToClassVenueResponseShouldReturnExpectedClassVenueResponse() {
      UniversityClassVenue venue = getUniversityClassVenue();

      UniversityClassVenueResponse expectedResponse = UniversityClassVenueResponse.builder()
          .id("id")
          .name("name")
          .description("description").build();

      assertThat(mapper.classVenueToClassVenueResponse(venue)).isEqualTo(expectedResponse);
  }

  @Test
  void classVenueToClassVenueEditRequestShouldReturnNullIfVenueIsNull() {
      UniversityClassVenue venue = null;
      assertThat(mapper.classVenueToClassVenueEditRequest(venue)).isEqualTo(null);
  }

  @Test
  void classVenueToClassVenueEditRequestShouldReturnExpectedClassVenueEditRequest() {
      UniversityClassVenue venue = getUniversityClassVenue();

      UniversityClassVenueEditRequest expectedRequest = UniversityClassVenueEditRequest.builder()
          .id("id")
          .name("name")
          .description("description").build();

      assertThat(mapper.classVenueToClassVenueEditRequest(venue)).isEqualTo(expectedRequest);
  }

  @Test
  void classVenueCreateRequestToClassVenueShouldReturnNullIfCreateRequestIsNull() {
      UniversityClassVenueCreateRequest request = null;
      assertThat(mapper.classVenueCreateRequestToClassVenue(request)).isEqualTo(null);
  }

  @Test
  void classVenueEditRequestToClassVenueShouldReturnNullIfEditRequestIsNull() {
      UniversityClassVenueEditRequest request = null;
      assertThat(mapper.classVenueEditRequestToClassVenue(request)).isEqualTo(null);
  }

  @Test
  void classVenueCreateRequestToClassVenueShouldReturnExpectedClassVenue() {
      UniversityClassVenueCreateRequest request = UniversityClassVenueCreateRequest.builder()
          .name("name")
          .description("description").build();

      UniversityClassVenue expectedVenue = getUniversityClassVenue();
      expectedVenue.setId(null);

      assertThat(mapper.classVenueCreateRequestToClassVenue(request)).isEqualTo(expectedVenue);
  }

  @Test
  void classVenueEditRequestToClassVenueShouldReturnExpectedClassVenue() {
      UniversityClassVenueEditRequest request = UniversityClassVenueEditRequest.builder()
          .id("id")
          .name("name")
          .description("description").build();

      UniversityClassVenue expectedVenue = getUniversityClassVenue();

      assertThat(mapper.classVenueEditRequestToClassVenue(request)).isEqualTo(expectedVenue);
  }

  private UniversityClassVenue getUniversityClassVenue() {
      return UniversityClassVenue.builder()
          .id("id")
          .name("name")
          .description("description")
          .build();
  }
}
