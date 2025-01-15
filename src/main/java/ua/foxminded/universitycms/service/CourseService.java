package ua.foxminded.universitycms.service;

import org.springframework.data.domain.Page;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;

public interface CourseService {
  Course saveFromRequest(CourseCreateRequest request);
  
  CourseResponse getCourseResponseById(String id);

  Course findByName(String name);
  
  CourseEditRequest getCourseEditRequestById(String id);
  
  Page<CourseResponse> getCourseResponses(String keyword, Integer itemsPerPage, Integer pageNumber);
  
  void editFromRequest(CourseEditRequest request);

  void deleteById(String id);
}
