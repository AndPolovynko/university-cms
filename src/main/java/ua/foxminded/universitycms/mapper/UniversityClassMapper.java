package ua.foxminded.universitycms.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UniversityClassMapper {
  @Mapping(target = "type", source = "type", qualifiedByName = "typeToString")
  @Mapping(target = "venue", source = "venue", qualifiedByName = "venueToString")
  @Mapping(target = "course", source = "course", qualifiedByName = "courseToString")
  @Mapping(target = "groupNames", source = "groups", qualifiedByName = "groupsToStrings")
  @Mapping(target = "teacherLogins", source = "teachers", qualifiedByName = "teachersToStrings")
  @Mapping(target = "dateTime", source = "dateTime", qualifiedByName = "offsetDateTimeToString")
  UniversityClassResponse classToClassResponse(UniversityClass universityClass);

  @Mapping(target = "type", source = "type", qualifiedByName = "typeToString")
  @Mapping(target = "venue", source = "venue", qualifiedByName = "venueToString")
  @Mapping(target = "course", source = "course", qualifiedByName = "courseToString")
  @Mapping(target = "groupNames", source = "groups", qualifiedByName = "groupsToStrings")
  @Mapping(target = "teacherLogins", source = "teachers", qualifiedByName = "teachersToStrings")
  @Mapping(target = "dateTime", source = "dateTime", qualifiedByName = "offsetDateTimeToString")
  UniversityClassEditRequest classToClassEditRequest(UniversityClass universityClass);

  @Mapping(target = "type", ignore = true)
  @Mapping(target = "venue", ignore = true)
  @Mapping(target = "course", ignore = true)
  @Mapping(target = "groups", ignore = true)
  @Mapping(target = "teachers", ignore = true)
  @Mapping(target = "dateTime", source = "dateTime", qualifiedByName = "stringToOffsetDateTime")
  UniversityClass classCreateRequestToClass(UniversityClassCreateRequest request);

  @Mapping(target = "type", ignore = true)
  @Mapping(target = "venue", ignore = true)
  @Mapping(target = "course", ignore = true)
  @Mapping(target = "groups", ignore = true)
  @Mapping(target = "teachers", ignore = true)
  @Mapping(target = "dateTime", ignore = true)
  UniversityClass classEditRequestToClass(UniversityClassEditRequest request);

  @Named("typeToString")
  default String typeToName(UniversityClassType type) {
    return type != null ? type.getName() : null;
  }

  @Named("venueToString")
  default String venueToString(UniversityClassVenue venue) {
    return venue != null ? venue.getName() : null;
  }

  @Named("courseToString")
  default String courseToString(Course course) {
    return course != null ? course.getName() : null;
  }

  @Named("groupsToStrings")
  default List<String> groupsToStrings(List<Group> groups) {
    return groups != null ? groups.stream()
        .map(Group::getName)
        .collect(Collectors.toList()) : null;
  }

  @Named("teachersToStrings")
  default List<String> teachersToStrings(List<User> teachers) {
    return teachers != null ? teachers.stream()
        .map(User::getLogin)
        .collect(Collectors.toList()) : null;
  }
  
  @Named("offsetDateTimeToString")
  default String offsetDateTimeToString(OffsetDateTime dateAndTime) {
    return dateAndTime != null ? dateAndTime.format(DateTimeFormatter.RFC_1123_DATE_TIME) : null;
  }
  
  @Named("stringToOffsetDateTime")
  default OffsetDateTime stringToOffsetDateTime(String dateAndTime) {
    return dateAndTime != null ? OffsetDateTime.of(LocalDateTime.parse(dateAndTime), ZoneOffset.UTC) : null;
  }
}
