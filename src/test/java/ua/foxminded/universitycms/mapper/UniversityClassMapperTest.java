package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.domain.userdetails.TeacherDetails;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;

public class UniversityClassMapperTest {
  UniversityClassMapper mapper = new UniversityClassMapperImpl();

  @Test
  void classToClassResponseShouldReturnNullIfClassIsNull() {
    UniversityClass universityClass = null;

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(null);
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponse() {
    UniversityClass universityClass = getUniversityClass();

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfDateAndTimeAreNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setDateTime(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime(null)
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfTypeIsNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setType(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type(null)
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfVenueIsNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setVenue(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue(null)
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfCourseIsNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setCourse(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course(null)
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfGroupsIsNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setGroups(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(null)
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassResponseShouldReturnExpectedResponseIfTeachersIsNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setTeachers(null);

    assertThat(mapper.classToClassResponse(universityClass)).isEqualTo(UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(null)
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassEditRequestShouldReturnNullIfClassIsNull() {
    UniversityClass universityClass = null;

    assertThat(mapper.classToClassEditRequest(universityClass)).isEqualTo(null);
  }

  @Test
  void classToClassEditRequestShouldReturnExpectedRequest() {
    UniversityClass universityClass = getUniversityClass();

    assertThat(mapper.classToClassEditRequest(universityClass)).isEqualTo(UniversityClassEditRequest.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime("Sat, 1 Jan 2000 12:00:00 +0530")
        .build());
  }

  @Test
  void classToClassEditRequestShouldReturnExpectedRequestIfDateAndTimeAreNull() {
    UniversityClass universityClass = getUniversityClass();
    universityClass.setDateTime(null);

    assertThat(mapper.classToClassEditRequest(universityClass)).isEqualTo(UniversityClassEditRequest.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-login-1", "teacher-login-2"))
        .dateTime(null)
        .build());
  }

  @Test
  void classCreateRequestToClassShouldReturnNullIfClassCreateRequestIsNull() {
    UniversityClassCreateRequest request = null;

    assertThat(mapper.classCreateRequestToClass(request)).isEqualTo(null);
  }

  @Test
  void classCreateRequestToClassShouldReturnExpectedResult() {
    UniversityClass universityClass = UniversityClass.builder()
        .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.UTC))
        .build();

    assertThat(mapper.classCreateRequestToClass(UniversityClassCreateRequest.builder()
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("Teach Teachson", "Teach2 Teachson2"))
        .dateTime("2000-01-01T12:00:00")
        .build())).isEqualTo(universityClass);
  }

  @Test
  void classCreateRequestToClassShouldReturnExpectedResultIfDateAndTimeAreNull() {
    UniversityClass universityClass = UniversityClass.builder().build();

    assertThat(mapper.classCreateRequestToClass(UniversityClassCreateRequest.builder()
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("Teach Teachson", "Teach2 Teachson2"))
        .dateTime(null)
        .build())).isEqualTo(universityClass);
  }

  @Test
  void classEditRequestToClassShouldReturnNullIfClassCreateRequestIsNull() {
    UniversityClassEditRequest request = null;

    assertThat(mapper.classEditRequestToClass(request)).isEqualTo(null);
  }

  @Test
  void classEditRequestToClassShouldReturnExpectedResult() {
    UniversityClass universityClass = UniversityClass.builder()
        .id("id")
        .dateTime(null)
        .build();

    assertThat(mapper.classEditRequestToClass(UniversityClassEditRequest.builder()
        .id("id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("Teach Teachson", "Teach2 Teachson2"))
        .dateTime("2000-01-01T12:00:00+05:30")
        .build())).isEqualTo(universityClass);
  }

  @Test
  void classEditRequestToClassShouldReturnExpectedResultIfDateAndTimeAreNull() {
    UniversityClass universityClass = UniversityClass.builder()
        .id("id").build();

    assertThat(mapper.classEditRequestToClass(UniversityClassEditRequest.builder()
        .id("id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("Teach Teachson", "Teach2 Teachson2"))
        .dateTime(null)
        .build())).isEqualTo(universityClass);
  }

  private UniversityClass getUniversityClass() {
    return UniversityClass.builder()
        .id("class-id")
        .type(UniversityClassType.builder()
            .id("type-id")
            .name("type").build())
        .venue(UniversityClassVenue.builder()
            .id("venue-id")
            .name("venue").build())
        .course(Course.builder()
            .id("course-id")
            .name("course")
            .description("description").build())
        .groups(List.of(
            Group.builder()
                .id("group-1-id")
                .name("group-1").build(),
            Group.builder()
                .id("group-2-id")
                .name("group-2").build()))
        .teachers(List.of(
            User.builder()
                .id("teacher-1-id")
                .login("teacher-login-1")
                .userDetailsList(List.of(
                    TeacherDetails.builder()
                        .id("teacher-details-1-id")
                        .firstName("Teach")
                        .lastName("Teachson").build()))
                .build(),
            User.builder()
                .id("teacher-2-id")
                .login("teacher-login-2")
                .userDetailsList(List.of(
                    TeacherDetails.builder()
                        .id("teacher-details-2-id")
                        .firstName("Teach2")
                        .lastName("Teachson2").build()))
                .build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.of("+05:30")))
        .build();
  }
}
