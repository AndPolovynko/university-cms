package ua.foxminded.universitycms.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CourseMapper {
  CourseResponse courseToCourseResponse(Course course);
  
  CourseEditRequest courseToCourseEditRequest(Course course);
  
  Course courseCreateRequestToCourse(CourseCreateRequest request);
  
  Course courseEditRequestToCourse(CourseEditRequest request);
}
