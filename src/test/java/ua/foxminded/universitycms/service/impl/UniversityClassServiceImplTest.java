package ua.foxminded.universitycms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

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
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.domain.userdetails.TeacherDetails;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;
import ua.foxminded.universitycms.dto.UserResponse;
import ua.foxminded.universitycms.mapper.UniversityClassMapper;
import ua.foxminded.universitycms.mapper.UserMapper;
import ua.foxminded.universitycms.repository.CourseRepository;
import ua.foxminded.universitycms.repository.GroupRepository;
import ua.foxminded.universitycms.repository.UniversityClassRepository;
import ua.foxminded.universitycms.repository.UniversityClassTypeRepository;
import ua.foxminded.universitycms.repository.UniversityClassVenueRepository;
import ua.foxminded.universitycms.repository.UserRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ExtendWith(MockitoExtension.class)
class UniversityClassServiceImplTest {
  @Mock
  UniversityClassRepository classRepo;
  @Mock
  UniversityClassTypeRepository typeRepo;
  @Mock
  UniversityClassVenueRepository venueRepo;
  @Mock
  CourseRepository courseRepo;
  @Mock
  GroupRepository groupRepo;
  @Mock
  UserRepository userRepo;
  @Mock
  UserMapper userMapper;
  @Mock
  UniversityClassMapper mapper;

  @InjectMocks
  UniversityClassServiceImpl service;

  @Captor
  ArgumentCaptor<UniversityClass> classCaptor;
  @Captor
  ArgumentCaptor<ArrayList<UniversityClass>> classListCaptor;
  @Captor
  ArgumentCaptor<UniversityClassVenue> venueCaptor;
  @Captor
  ArgumentCaptor<UniversityClassType> typeCaptor;
  @Captor
  ArgumentCaptor<Group> groupCaptor;
  @Captor
  ArgumentCaptor<User> userCaptor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .name("course").build()));
    when(groupRepo.findByName(anyString()))
        .thenReturn(Optional.of(Group.builder().name("group").build()));
    when(userRepo.findByLogin(anyString()))
        .thenReturn(Optional.of(User.builder().build()));

    service.saveFromRequest(getCreateRequest());

    verify(mapper, atLeastOnce()).classCreateRequestToClass(getCreateRequest());
  }

  @Test
  void saveFromRequestShouldCallUniversityClassRepositorySaveMethodWithExpectedUniversityClass() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(UniversityClass.builder()
            .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.of("+05:30")))
            .build());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .id("type-id")
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .id("venue-id")
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .id("course-id")
        .name("course")
        .description("description").build()));
    when(groupRepo.findByName("group-1"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-1-id")
            .name("group-1").build()));
    when(groupRepo.findByName("group-2"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-2-id")
            .name("group-2").build()));
    when(userRepo.findByLogin("teacher-1"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-1-id")
            .login("teacher-1")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-1-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));
    when(userRepo.findByLogin("teacher-2"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-2-id")
            .login("teacher-2")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-2-id")
                    .firstName("Teach2")
                    .lastName("Teachson2").build()))
            .build()));

    UniversityClass expectedClass = getUniversityClass();
    expectedClass.setId(null);

    service.saveFromRequest(getCreateRequest());

    verify(classRepo).save(classCaptor.capture());
    assertThat(classCaptor.getValue()).isEqualTo(expectedClass);
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfVenueWithGivenNameDoesNotExist() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest()));

    assertThat(exception.getMessage()).contains("venue");
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfCourseWithGivenNameDoesNotExist() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .name("venue").build()));

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest()));

    assertThat(exception.getMessage()).contains("course");
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfGroupWithGivenNameDoesNotExist() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .name("course").build()));

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest()));

    assertThat(exception.getMessage()).contains("group-1");
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfTeacherWithGivenLoginDoesNotExist() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .name("course").build()));
    when(groupRepo.findByName(anyString()))
        .thenReturn(Optional.of(Group.builder().name("group").build()));

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest()));

    assertThat(exception.getMessage()).contains("teacher-1");
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfTypeWithGivenNameDoesNotExist() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest()));

    assertThat(exception.getMessage()).contains("type");
  }

  @Test
  void saveFromRequestWithFrequencyShouldCallMapper() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getUniversityClass());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .name("course").build()));
    when(groupRepo.findByName(anyString()))
        .thenReturn(Optional.of(Group.builder().name("group").build()));
    when(userRepo.findByLogin(anyString()))
        .thenReturn(Optional.of(User.builder().build()));

    service.saveFromRequest(getCreateRequestWithUtcTime(), "daily", "2000-04-01");

    verify(mapper, atLeastOnce()).classCreateRequestToClass(getCreateRequestWithUtcTime());
  }

  @Test
  void saveFromRequestWithFrequencyShouldSaveCorrectNumberOfClassesWithWeeklyRepeatFrequency() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(UniversityClass.builder()
            .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.of("+00:00")))
            .build());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .id("type-id")
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .id("venue-id")
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .id("course-id")
        .name("course")
        .description("description").build()));
    when(groupRepo.findByName("group-1"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-1-id")
            .name("group-1").build()));
    when(groupRepo.findByName("group-2"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-2-id")
            .name("group-2").build()));
    when(userRepo.findByLogin("teacher-1"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-1-id")
            .login("teacher-1")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-1-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));
    when(userRepo.findByLogin("teacher-2"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-2-id")
            .login("teacher-2")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-2-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));

    service.saveFromRequest(getCreateRequestWithUtcTime(), "weekly", "2000-01-25");

    verify(classRepo).saveAll(classListCaptor.capture());
    assertThat(classListCaptor.getValue().size()).isEqualTo(4);
  }

  @Test
  void saveFromRequestWithFrequencyShouldSaveCorrectNumberOfClassesWithMonthlyRepeatFrequenc() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(UniversityClass.builder()
            .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.of("+00:00")))
            .build());
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .id("type-id")
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .id("venue-id")
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .id("course-id")
        .name("course")
        .description("description").build()));
    when(groupRepo.findByName("group-1"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-1-id")
            .name("group-1").build()));
    when(groupRepo.findByName("group-2"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-2-id")
            .name("group-2").build()));
    when(userRepo.findByLogin("teacher-1"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-1-id")
            .login("teacher-1")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-1-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));
    when(userRepo.findByLogin("teacher-2"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-2-id")
            .login("teacher-2")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-2-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));

    service.saveFromRequest(getCreateRequestWithUtcTime(), "monthly", "2000-04-01");

    verify(classRepo).saveAll(classListCaptor.capture());
    assertThat(classListCaptor.getValue().size()).isEqualTo(4);
  }

  @Test
  void saveFromRequestWithFrequencyShouldThrowEntityNotFoundRuntimeExceptionIfTypeNotFound() {
    when(mapper.classCreateRequestToClass(any(UniversityClassCreateRequest.class)))
        .thenReturn(getEmptyUniversityClass());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(getCreateRequest(), "daily", "2000-04-01"));

    assertThat(exception.getMessage()).contains("type");
  }

  @Test
  void getUniversityClassResponseByIdShouldReturnExpectedResult() {
    when(classRepo.findById("class-id")).thenReturn(Optional.of(getUniversityClass()));
    when(mapper.classToClassResponse(getUniversityClass())).thenReturn(getResoponse());

    assertThat(service.getUniversityClassResponseById("class-id")).isEqualTo(getResoponse());
  }

  @Test
  void getUniversityClassResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfClassWithGivenIdDoesNotExist() {
    when(classRepo.findById("test-id")).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUniversityClassResponseById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void getUniversityClassEditRequestByIdShouldReturnExpectedResult() {
    when(classRepo.findById("class-id")).thenReturn(Optional.of(getUniversityClass()));
    when(mapper.classToClassEditRequest(getUniversityClass())).thenReturn(getEditRequest());

    assertThat(service.getUniversityClassEditRequestById("class-id")).isEqualTo(getEditRequest());
  }

  @Test
  void getUniversityClassEditRequestByIdShouldThrowEntityNotFoundRuntimeExceptionIfClassWithGivenIdDoesNotExist() {
    when(classRepo.findById("test-id")).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUniversityClassEditRequestById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void getUniversityClassResponsesShouldCallMapperIfKeywordIsNull() {
    when(classRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses(null, 1, "0");

    verify(mapper, atLeastOnce()).classToClassResponse(any());
  }

  @Test
  void getUniversityClassResponsesShouldCallRepoFindAllIfKeywordIsNull() {
    when(classRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses(null, 1, "0");

    verify(classRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getUniversityClassResponsesShouldCallRepoFindAllIfKeywordIsBlank() {
    when(classRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses("", 1, "0");

    verify(classRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getUniversityClassResponsesShouldCallMapperIfKeywordIsNotNull() {
    when(classRepo.findByTypeNameContainingOrVenueNameContainingOrCourseNameContaining(any(), any(), any(),
        any(PageRequest.class)))
        .thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses("keyword", 1, "0");

    verify(mapper, atLeastOnce()).classToClassResponse(any());
  }

  @Test
  void getUniversityClassResponsesShouldCallRepoFindAllIfPageNumberIsNegative() {
    when(classRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses("", 1, "-1");

    verify(classRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getUniversityClassResponsesShouldCallRepoFindAllIfPageNumberIsPositive() {
    when(classRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses("", 1, "2");

    verify(classRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getUniversityClassResponsesShouldCallRepoFindByTypeContainingOrVenueContainingOrCourseNameContainingIfKeywordIsNotNull() {
    when(classRepo.findByTypeNameContainingOrVenueNameContainingOrCourseNameContaining(any(), any(), any(),
        any(PageRequest.class)))
        .thenReturn(new PageImpl<UniversityClass>(getUniversityClasses()));

    service.getUniversityClassResponses("keyword", 1, "0");

    verify(classRepo, atLeastOnce()).findByTypeNameContainingOrVenueNameContainingOrCourseNameContaining(any(), any(),
        any(),
        any(PageRequest.class));
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndGroupNameShouldCallRepoFindByDateBetweenAndGroupsNameOrderByDateAscTimeAsc() {
    when(classRepo.findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class), anyString())).thenReturn(getUniversityClasses());
    when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(User.builder().build()));
    when(userMapper.userToUserResponse(any(User.class))).thenReturn(UserResponse.builder()
        .groupName("group").build());
    when(mapper.classToClassResponse(any(UniversityClass.class))).thenReturn(getResoponse());

    service.getUniversityClassResponsesByDateRangeAndGroupName(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "group");

    verify(classRepo, atLeastOnce()).findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "group");
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndGroupNameShouldReturnExpectedResult() {
    UniversityClass class1 = UniversityClass.builder()
        .id("1")
        .type(UniversityClassType.builder().name("Lecture").build())
        .venue(UniversityClassVenue.builder().name("Room 101").build())
        .course(Course.builder().name("Mathematics").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher1").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 2), LocalTime.of(10, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class2 = UniversityClass.builder()
        .id("2")
        .type(UniversityClassType.builder().name("Lab").build())
        .venue(UniversityClassVenue.builder().name("Room 202").build())
        .course(Course.builder().name("Physics").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher2").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 3), LocalTime.of(14, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class3 = UniversityClass.builder()
        .id("3")
        .type(UniversityClassType.builder().name("Seminar").build())
        .venue(UniversityClassVenue.builder().name("Room 303").build())
        .course(Course.builder().name("Chemistry").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher3").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 10), LocalTime.of(9, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClassResponse classResponse1 = UniversityClassResponse.builder()
        .id("1")
        .type("Lecture")
        .venue("Room 101")
        .course("Mathematics")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher1"))
        .dateTime("2024-12-01T10:00+05:30")
        .build();

    UniversityClassResponse classResponse2 = UniversityClassResponse.builder()
        .id("2")
        .type("Lab")
        .venue("Room 202")
        .course("Physics")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher2"))
        .dateTime("2024-12-02T14:00+05:30")
        .build();

    UniversityClassResponse classResponse3 = UniversityClassResponse.builder()
        .id("3")
        .type("Seminar")
        .venue("Room 303")
        .course("Chemistry")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher3"))
        .dateTime("2024-12-10T09:00+05:30")
        .build();

    Map<String, Map<String, List<UniversityClassResponse>>> expectedResult = new HashMap<>();

    Map<String, List<UniversityClassResponse>> week1 = new HashMap<>();
    week1.put("02-12-2024", List.of(classResponse1));
    week1.put("03-12-2024", List.of(classResponse2));
    expectedResult.put("Week 49", week1);

    Map<String, List<UniversityClassResponse>> week2 = new HashMap<>();
    week2.put("10-12-2024", List.of(classResponse3));
    expectedResult.put("Week 50", week2);

    when(classRepo.findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class),
        anyString())).thenReturn(List.of(class1, class2, class3));
    when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(User.builder().build()));
    when(userMapper.userToUserResponse(any(User.class))).thenReturn(UserResponse.builder()
        .groupName("group").build());

    when(mapper.classToClassResponse(class1)).thenReturn(classResponse1);
    when(mapper.classToClassResponse(class2)).thenReturn(classResponse2);
    when(mapper.classToClassResponse(class3)).thenReturn(classResponse3);

    assertThat(service.getUniversityClassResponsesByDateRangeAndGroupName(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "group")).isEqualTo(expectedResult);
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndGroupNameWithCustomRangeArgumentShouldReturnExpectedResult() {
    UniversityClass class1 = UniversityClass.builder()
        .id("1")
        .type(UniversityClassType.builder().name("Lecture").build())
        .venue(UniversityClassVenue.builder().name("Room 101").build())
        .course(Course.builder().name("Mathematics").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher1").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 2), LocalTime.of(10, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class2 = UniversityClass.builder()
        .id("2")
        .type(UniversityClassType.builder().name("Lab").build())
        .venue(UniversityClassVenue.builder().name("Room 202").build())
        .course(Course.builder().name("Physics").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher2").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 3), LocalTime.of(14, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class3 = UniversityClass.builder()
        .id("3")
        .type(UniversityClassType.builder().name("Seminar").build())
        .venue(UniversityClassVenue.builder().name("Room 303").build())
        .course(Course.builder().name("Chemistry").build())
        .groups(List.of(Group.builder().name("group").build()))
        .teachers(List.of(User.builder().login("teacher3").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 10), LocalTime.of(9, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClassResponse classResponse1 = UniversityClassResponse.builder()
        .id("1")
        .type("Lecture")
        .venue("Room 101")
        .course("Mathematics")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher1"))
        .dateTime("2024-12-01T10:00+05:30")
        .build();

    UniversityClassResponse classResponse2 = UniversityClassResponse.builder()
        .id("2")
        .type("Lab")
        .venue("Room 202")
        .course("Physics")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher2"))
        .dateTime("2024-12-02T14:00+05:30")
        .build();

    UniversityClassResponse classResponse3 = UniversityClassResponse.builder()
        .id("3")
        .type("Seminar")
        .venue("Room 303")
        .course("Chemistry")
        .groupNames(List.of("group"))
        .teacherLogins(List.of("teacher3"))
        .dateTime("2024-12-10T09:00+05:30")
        .build();

    Map<String, Map<String, List<UniversityClassResponse>>> expectedResult = new HashMap<>();

    Map<String, List<UniversityClassResponse>> week1 = new HashMap<>();
    week1.put("02-12-2024", List.of(classResponse1));
    week1.put("03-12-2024", List.of(classResponse2));
    expectedResult.put("Week 49", week1);

    Map<String, List<UniversityClassResponse>> week2 = new HashMap<>();
    week2.put("10-12-2024", List.of(classResponse3));
    expectedResult.put("Week 50", week2);

    when(classRepo.findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class),
        anyString())).thenReturn(List.of(class1, class2, class3));
    when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(User.builder().build()));
    when(userMapper.userToUserResponse(any(User.class))).thenReturn(UserResponse.builder()
        .groupName("group").build());

    when(mapper.classToClassResponse(class1)).thenReturn(classResponse1);
    when(mapper.classToClassResponse(class2)).thenReturn(classResponse2);
    when(mapper.classToClassResponse(class3)).thenReturn(classResponse3);

    assertThat(service.getUniversityClassResponsesByDateRangeAndGroupName("2024-12-01_2024-12-31",
        TimeZone.getTimeZone("Asia/Kolkata"), "group")).isEqualTo(expectedResult);
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndGroupNameShouldThrowEntityNotFoundRuntimeExceptionIfUserWithStudentLoginDoesNotExist() {
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUniversityClassResponsesByDateRangeAndGroupName(OffsetDateTime.now(), OffsetDateTime.now(),
            "login404"));

    assertThat(exception.getMessage()).contains("login404");
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndTeacherLoginShouldCallRepofindByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc() {
    when(classRepo.findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class), anyString())).thenReturn(getUniversityClasses());
    when(mapper.classToClassResponse(any(UniversityClass.class))).thenReturn(getResoponse());

    service.getUniversityClassResponsesByDateRangeAndTeacherLogin(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "login");

    verify(classRepo, atLeastOnce()).findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "login");
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndTeacherLoginShouldReturnExpectedResult() {
    UniversityClass class1 = UniversityClass.builder()
        .id("1")
        .type(UniversityClassType.builder().name("Lecture").build())
        .venue(UniversityClassVenue.builder().name("Room 101").build())
        .course(Course.builder().name("Mathematics").build())
        .groups(List.of(Group.builder().name("group1").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 2), LocalTime.of(10, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class2 = UniversityClass.builder()
        .id("2")
        .type(UniversityClassType.builder().name("Lab").build())
        .venue(UniversityClassVenue.builder().name("Room 202").build())
        .course(Course.builder().name("Physics").build())
        .groups(List.of(Group.builder().name("group2").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 3), LocalTime.of(14, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class3 = UniversityClass.builder()
        .id("3")
        .type(UniversityClassType.builder().name("Seminar").build())
        .venue(UniversityClassVenue.builder().name("Room 303").build())
        .course(Course.builder().name("Chemistry").build())
        .groups(List.of(Group.builder().name("group3").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 10), LocalTime.of(9, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClassResponse classResponse1 = UniversityClassResponse.builder()
        .id("1")
        .type("Lecture")
        .venue("Room 101")
        .course("Mathematics")
        .groupNames(List.of("group1"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-01T10:00+05:30")
        .build();

    UniversityClassResponse classResponse2 = UniversityClassResponse.builder()
        .id("2")
        .type("Lab")
        .venue("Room 202")
        .course("Physics")
        .groupNames(List.of("group2"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-02T14:00+05:30")
        .build();

    UniversityClassResponse classResponse3 = UniversityClassResponse.builder()
        .id("3")
        .type("Seminar")
        .venue("Room 303")
        .course("Chemistry")
        .groupNames(List.of("group3"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-10T09:00+05:30")
        .build();

    Map<String, Map<String, List<UniversityClassResponse>>> expectedResult = new HashMap<>();

    Map<String, List<UniversityClassResponse>> week1 = new HashMap<>();
    week1.put("02-12-2024", List.of(classResponse1));
    week1.put("03-12-2024", List.of(classResponse2));
    expectedResult.put("Week 49", week1);

    Map<String, List<UniversityClassResponse>> week2 = new HashMap<>();
    week2.put("10-12-2024", List.of(classResponse3));
    expectedResult.put("Week 50", week2);

    when(classRepo.findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class),
        anyString())).thenReturn(List.of(class1, class2, class3));

    when(mapper.classToClassResponse(class1)).thenReturn(classResponse1);
    when(mapper.classToClassResponse(class2)).thenReturn(classResponse2);
    when(mapper.classToClassResponse(class3)).thenReturn(classResponse3);

    assertThat(service.getUniversityClassResponsesByDateRangeAndTeacherLogin(
        OffsetDateTime.of(LocalDate.of(2024, 12, 1), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        OffsetDateTime.of(LocalDate.of(2024, 12, 31), LocalTime.of(0, 0), ZoneOffset.of("+05:30")),
        "teacher")).isEqualTo(expectedResult);
  }

  @Test
  void getUniversityClassResponsesByDateRangeAndTeacherLoginWithCustomRangeArgumentShouldReturnExpectedResult() {
    UniversityClass class1 = UniversityClass.builder()
        .id("1")
        .type(UniversityClassType.builder().name("Lecture").build())
        .venue(UniversityClassVenue.builder().name("Room 101").build())
        .course(Course.builder().name("Mathematics").build())
        .groups(List.of(Group.builder().name("group1").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 2), LocalTime.of(10, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class2 = UniversityClass.builder()
        .id("2")
        .type(UniversityClassType.builder().name("Lab").build())
        .venue(UniversityClassVenue.builder().name("Room 202").build())
        .course(Course.builder().name("Physics").build())
        .groups(List.of(Group.builder().name("group2").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 3), LocalTime.of(14, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClass class3 = UniversityClass.builder()
        .id("3")
        .type(UniversityClassType.builder().name("Seminar").build())
        .venue(UniversityClassVenue.builder().name("Room 303").build())
        .course(Course.builder().name("Chemistry").build())
        .groups(List.of(Group.builder().name("group3").build()))
        .teachers(List.of(User.builder().login("teacher").build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2024, 12, 10), LocalTime.of(9, 0), ZoneOffset.of("+05:30")))
        .build();

    UniversityClassResponse classResponse1 = UniversityClassResponse.builder()
        .id("1")
        .type("Lecture")
        .venue("Room 101")
        .course("Mathematics")
        .groupNames(List.of("group1"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-01T10:00+05:30")
        .build();

    UniversityClassResponse classResponse2 = UniversityClassResponse.builder()
        .id("2")
        .type("Lab")
        .venue("Room 202")
        .course("Physics")
        .groupNames(List.of("group2"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-02T14:00+05:30")
        .build();

    UniversityClassResponse classResponse3 = UniversityClassResponse.builder()
        .id("3")
        .type("Seminar")
        .venue("Room 303")
        .course("Chemistry")
        .groupNames(List.of("group3"))
        .teacherLogins(List.of("teacher"))
        .dateTime("2024-12-10T09:00+05:30")
        .build();

    Map<String, Map<String, List<UniversityClassResponse>>> expectedResult = new HashMap<>();

    Map<String, List<UniversityClassResponse>> week1 = new HashMap<>();
    week1.put("02-12-2024", List.of(classResponse1));
    week1.put("03-12-2024", List.of(classResponse2));
    expectedResult.put("Week 49", week1);

    Map<String, List<UniversityClassResponse>> week2 = new HashMap<>();
    week2.put("10-12-2024", List.of(classResponse3));
    expectedResult.put("Week 50", week2);

    when(classRepo.findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(any(OffsetDateTime.class),
        any(OffsetDateTime.class),
        anyString())).thenReturn(List.of(class1, class2, class3));

    when(mapper.classToClassResponse(class1)).thenReturn(classResponse1);
    when(mapper.classToClassResponse(class2)).thenReturn(classResponse2);
    when(mapper.classToClassResponse(class3)).thenReturn(classResponse3);

    assertThat(service.getUniversityClassResponsesByDateRangeAndTeacherLogin("2024-12-01_2024-12-31",
        TimeZone.getTimeZone("Asia/Kolkata"), "group")).isEqualTo(expectedResult);
  }

  @Test
  void editFromRequestShouldCallUniversityClassRepositorySaveMethodWithExpectedUniversityClass() {
    when(classRepo.findById("id")).thenReturn(Optional.of(getUniversityClass()));
    when(typeRepo.findByName("type")).thenReturn(Optional.of(UniversityClassType.builder()
        .id("type-id")
        .name("type").build()));
    when(venueRepo.findByName("venue")).thenReturn(Optional.of(UniversityClassVenue.builder()
        .id("venue-id")
        .name("venue").build()));
    when(courseRepo.findByName("course")).thenReturn(Optional.of(Course.builder()
        .id("course-id")
        .name("course")
        .description("description").build()));
    when(groupRepo.findByName("group-1"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-1-id")
            .name("group-1").build()));
    when(groupRepo.findByName("group-2"))
        .thenReturn(Optional.of(Group.builder()
            .id("group-2-id")
            .name("group-2").build()));
    when(userRepo.findByLogin("teacher-1"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-1-id")
            .login("teacher-1")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-1-id")
                    .firstName("Teach")
                    .lastName("Teachson").build()))
            .build()));
    when(userRepo.findByLogin("teacher-2"))
        .thenReturn(Optional.of(User.builder()
            .id("teacher-2-id")
            .login("teacher-2")
            .userDetailsList(List.of(
                TeacherDetails.builder()
                    .id("teacher-details-2-id")
                    .firstName("Teach2")
                    .lastName("Teachson2").build()))
            .build()));

    UniversityClass expectedClass = getUniversityClass();

    service.editFromRequest(getEditRequest());

    verify(classRepo).save(classCaptor.capture());
    assertThat(classCaptor.getValue()).isEqualTo(expectedClass);
  }

  @Test
  void editFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfClassWithGivenIdDoesNotExist() {
    when(classRepo.findById("test-id")).thenReturn(Optional.empty());

    UniversityClassEditRequest request = getEditRequest();
    request.setId("test-id");

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editFromRequest(request));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(classRepo).deleteById(any());

    service.deleteById("id");

    verify(classRepo, atLeastOnce()).deleteById("id");
  }

  private UniversityClass getEmptyUniversityClass() {
    return UniversityClass.builder().build();
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
                .login("teacher-1")
                .userDetailsList(List.of(
                    TeacherDetails.builder()
                        .id("teacher-details-1-id")
                        .firstName("Teach")
                        .lastName("Teachson").build()))
                .build(),
            User.builder()
                .id("teacher-2-id")
                .login("teacher-2")
                .userDetailsList(List.of(
                    TeacherDetails.builder()
                        .id("teacher-details-2-id")
                        .firstName("Teach2")
                        .lastName("Teachson2").build()))
                .build()))
        .dateTime(OffsetDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(12, 00), ZoneOffset.of("+05:30")))
        .build();
  }

  private List<UniversityClass> getUniversityClasses() {
    return List.of(getUniversityClass(), getUniversityClass());
  }

  private UniversityClassCreateRequest getCreateRequest() {
    return UniversityClassCreateRequest.builder()
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-1", "teacher-2"))
        .dateTime("2000-01-01T12:00+05:30")
        .build();
  }

  private UniversityClassCreateRequest getCreateRequestWithUtcTime() {
    return UniversityClassCreateRequest.builder()
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-1", "teacher-2"))
        .dateTime("2000-01-01T12:00+00:00")
        .build();
  }

  private UniversityClassEditRequest getEditRequest() {
    return UniversityClassEditRequest.builder()
        .id("id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("teacher-1", "teacher-2"))
        .dateTime("2000-01-01T12:00+05:30")
        .build();
  }

  private UniversityClassResponse getResoponse() {
    return UniversityClassResponse.builder()
        .id("class-id")
        .type("type")
        .venue("venue")
        .course("course")
        .groupNames(List.of("group-1", "group-2"))
        .teacherLogins(List.of("Teach Teachson", "Teach2 Teachson2"))
        .dateTime("2000-01-01T12:00+05:30")
        .build();
  }
}
