package ua.foxminded.universitycms.service.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.RepeatFrequency;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;
import ua.foxminded.universitycms.mapper.UniversityClassMapper;
import ua.foxminded.universitycms.mapper.UserMapper;
import ua.foxminded.universitycms.repository.CourseRepository;
import ua.foxminded.universitycms.repository.GroupRepository;
import ua.foxminded.universitycms.repository.UniversityClassRepository;
import ua.foxminded.universitycms.repository.UniversityClassTypeRepository;
import ua.foxminded.universitycms.repository.UniversityClassVenueRepository;
import ua.foxminded.universitycms.repository.UserRepository;
import ua.foxminded.universitycms.service.UniversityClassService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;
import ua.foxminded.universitycms.service.util.PageNumberParser;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UniversityClassServiceImpl implements UniversityClassService {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  private final UniversityClassRepository repo;
  private final UniversityClassTypeRepository typeRepo;
  private final UniversityClassVenueRepository venueRepo;
  private final CourseRepository courseRepo;
  private final GroupRepository groupRepo;
  private final UserRepository userRepo;
  private final UniversityClassMapper mapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UniversityClass saveFromRequest(UniversityClassCreateRequest request) {
    return repo.save(mapRequestToClass(mapper.classCreateRequestToClass(request),
        request.getType(), request.getVenue(), request.getCourse(), request.getGroupNames(),
        request.getTeacherLogins()));
  }

  @Override
  @Transactional
  public Iterable<UniversityClass> saveFromRequest(UniversityClassCreateRequest request, String repeat,
      String repeatUntil) {
    OffsetDateTime repeatUntilDate = OffsetDateTime.parse(repeatUntil + "T23:59:59+00:00");
    RepeatFrequency frequency = RepeatFrequency.fromString(repeat);

    UniversityClass universityClass = mapRequestToClass(mapper.classCreateRequestToClass(request),
        request.getType(), request.getVenue(), request.getCourse(), request.getGroupNames(),
        request.getTeacherLogins());

    List<UniversityClass> classesForSave = new ArrayList<>();
    classesForSave.add(universityClass);

    OffsetDateTime firstClassDateTime = universityClass.getDateTime();
    if (frequency == RepeatFrequency.DAILY) {
      classesForSave.addAll(createCopies(universityClass,
          ChronoUnit.DAYS.between(firstClassDateTime, repeatUntilDate), firstClassDateTime));
    } else if (frequency == RepeatFrequency.WEEKLY) {
      classesForSave.addAll(createCopies(universityClass,
          ChronoUnit.WEEKS.between(firstClassDateTime, repeatUntilDate), firstClassDateTime));
    } else {
      classesForSave.addAll(createCopies(universityClass,
          ChronoUnit.MONTHS.between(firstClassDateTime, repeatUntilDate), firstClassDateTime));
    }

    return repo.saveAll(classesForSave);
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassResponse getUniversityClassResponseById(String id) {
    return mapper.classToClassResponse(repo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("University class with id = " + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassEditRequest getUniversityClassEditRequestById(String id) {
    return mapper.classToClassEditRequest(repo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("University class with id = " + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityClassResponse> getUniversityClassResponses(String keyword, Integer itemsPerPage,
      String pageNumber) {
    if (keyword == null || keyword.isBlank()) {
      return repo.findAll(PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("dateTime")))
          .map(mapper::classToClassResponse);
    } else {
      return repo
          .findByTypeNameContainingOrVenueNameContainingOrCourseNameContaining(keyword, keyword, keyword,
              PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("date")))
          .map(mapper::classToClassResponse);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndGroupName(
      OffsetDateTime startDate, OffsetDateTime endDate, String studentLogin) {

    ZoneOffset requestOffset = startDate.getOffset();

    String groupName = userMapper.userToUserResponse(userRepo.findByLogin(studentLogin).orElseThrow(
        () -> new EntityNotFoundRuntimeException("User with login = " + studentLogin + " doesn't exist.")))
        .getGroupName();
    List<UniversityClass> classes = repo
        .findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(startDate, endDate, groupName);

    return groupClasses(requestOffset, classes);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndGroupName(
      String customRange, TimeZone timeZone, String studentLogin) {

    OffsetDateTime startDate = OffsetDateTime.of(LocalDate.parse(customRange.split("_")[0]), LocalTime.MIN,
        timeZone.toZoneId().getRules().getOffset(Instant.now()));

    OffsetDateTime endDate = OffsetDateTime.of(LocalDate.parse(customRange.split("_")[1]), LocalTime.MIN,
        timeZone.toZoneId().getRules().getOffset(Instant.now()));

    return getUniversityClassResponsesByDateRangeAndGroupName(startDate, endDate, studentLogin);
  }

  @Override
  public Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndTeacherLogin(
      OffsetDateTime startDate, OffsetDateTime endDate, String teacherLogin) {

    ZoneOffset requestOffset = startDate.getOffset();

    List<UniversityClass> classes = repo
        .findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(startDate, endDate, teacherLogin);

    return groupClasses(requestOffset, classes);
  }

  @Override
  public Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndTeacherLogin(
      String customRange, TimeZone timeZone, String teacherLogin) {

    OffsetDateTime startDate = OffsetDateTime.of(LocalDate.parse(customRange.split("_")[0]), LocalTime.MIN,
        timeZone.toZoneId().getRules().getOffset(Instant.now()));

    OffsetDateTime endDate = OffsetDateTime.of(LocalDate.parse(customRange.split("_")[1]), LocalTime.MIN,
        timeZone.toZoneId().getRules().getOffset(Instant.now()));

    return getUniversityClassResponsesByDateRangeAndTeacherLogin(startDate, endDate, teacherLogin);
  }

  @Override
  @Transactional
  public void editFromRequest(UniversityClassEditRequest request) {
    repo.save(mapRequestToClass(repo.findById(request.getId()).orElseThrow(
        () -> new EntityNotFoundRuntimeException("University class with id = " + request.getId() + " doesn't exist.")),
        request.getType(), request.getVenue(), request.getCourse(), request.getGroupNames(),
        request.getTeacherLogins()));
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    repo.deleteById(id);
  }

  private UniversityClass mapRequestToClass(UniversityClass universityClass, String type, String venue,
      String course, List<String> groups, List<String> teachers) {

    universityClass.setType(typeRepo.findByName(type).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Type with name = " + type + " doesn't exist.")));

    universityClass.setVenue(venueRepo.findByName(venue).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Venue with name = " + venue + " doesn't exist.")));

    universityClass.setCourse(courseRepo.findByName(course).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Course with name = " + course + " doesn't exist.")));

    universityClass.setGroups(new ArrayList<Group>());
    groups.forEach(group -> {
      universityClass.getGroups().add(groupRepo.findByName(group).orElseThrow(
          () -> new EntityNotFoundRuntimeException("Group with name = " + group + " doesn't exist.")));
    });

    universityClass.setTeachers(new ArrayList<User>());
    teachers.forEach(teacher -> {
      universityClass.getTeachers().add(userRepo.findByLogin(teacher).orElseThrow(
          () -> new EntityNotFoundRuntimeException("User with login = " + teacher + " doesn't exist.")));
    });

    return universityClass;
  }

  private Map<String, Map<String, List<UniversityClassResponse>>> groupClasses(ZoneOffset requestOffset,
      List<UniversityClass> classes) {

    classes.forEach(universityClass -> {
      universityClass.setDateTime(universityClass.getDateTime().withOffsetSameInstant(requestOffset));
    });

    Map<String, Map<String, List<UniversityClassResponse>>> groupedClasses = classes.stream()
        .collect(Collectors.groupingBy(
            universityClass -> {
              return "Week " + universityClass.getDateTime().get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfYear());
            },
            TreeMap::new,
            Collectors.groupingBy(
                universityClass -> universityClass.getDateTime().toLocalDate()
                    .format(DATE_FORMATTER),
                TreeMap::new,
                Collectors.mapping(universityClass -> {
                  UniversityClassResponse classResponse = mapper.classToClassResponse(universityClass);
                  classResponse.setDateTime(universityClass.getDateTime().toLocalTime().toString());
                  return classResponse;
                }, Collectors.toList()))));

    return groupedClasses;
  }

  private List<UniversityClass> createCopies(UniversityClass classForCopying, Long copyNumber,
      OffsetDateTime firstClassDateTime) {
    List<UniversityClass> copies = new ArrayList<>();

    for (int i = 1; i <= copyNumber; i++) {
      UniversityClass universityClassCopy = classForCopying.toBuilder().build();
      universityClassCopy.setDateTime(firstClassDateTime.plusDays(i));
      copies.add(universityClassCopy);
    }

    return copies;
  }
}
