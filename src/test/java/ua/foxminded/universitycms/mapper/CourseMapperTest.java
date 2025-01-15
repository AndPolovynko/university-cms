package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;

class CourseMapperTest {
  CourseMapper mapper = new CourseMapperImpl();
  
  @Test
  void courseToCourseResponseShouldReturnNullIfCourseIsNull() {
    Course course = null;
    assertThat(mapper.courseToCourseResponse(course)).isEqualTo(null);
  }
  
  @Test
  void courseToCourseResponseShouldReturnExpectedCourseResponse() {
    Course course = getCourse();
    
    CourseResponse expectedResponse = CourseResponse.builder()
        .id("id")
        .name("name")
        .description("description").build();
        
    assertThat(mapper.courseToCourseResponse(course)).isEqualTo(expectedResponse);
  }
  
  @Test
  void courseToCourseEditRequestShouldReturnNullIfCourseIsNull() {
    Course course = null;
    assertThat(mapper.courseToCourseEditRequest(course)).isEqualTo(null);
  }
  
  @Test
  void courseToCourseEditRequestShouldReturnExpectedCourseEditRequest() {
    Course course = getCourse();
    
    CourseEditRequest expectedRequest = CourseEditRequest.builder()
        .id("id")
        .name("name")
        .description("description").build();
        
    assertThat(mapper.courseToCourseEditRequest(course)).isEqualTo(expectedRequest);
  }
  
  @Test
  void courseCreateRequestToCourseShouldReturnNullIfCourseCreateRequestIsNull() {
    CourseCreateRequest request = null;
    assertThat(mapper.courseCreateRequestToCourse(request)).isEqualTo(null);
  }
  
  @Test
  void courseEditRequestToCourseShouldReturnNullIfCourseEditRequestToCourseIsNull() {
    CourseEditRequest course = null;
    assertThat(mapper.courseEditRequestToCourse(course)).isEqualTo(null);
  }
  
  @Test
  void courseCreateRequestToCourseShouldReturnExpectedCourse() {
    CourseCreateRequest request = CourseCreateRequest.builder()
        .name("name")
        .description("description").build();
    
    Course expectedCourse = getCourse();
    expectedCourse.setId(null);
    
    assertThat(mapper.courseCreateRequestToCourse(request)).isEqualTo(expectedCourse);
  }
  
  @Test
  void courseEditRequestToCourseShouldReturnExpectedCourse() {
    CourseEditRequest request = CourseEditRequest.builder()
        .id("id")
        .name("name")
        .description("description").build();
    
    Course expectedCourse = getCourse();
    
    assertThat(mapper.courseEditRequestToCourse(request)).isEqualTo(expectedCourse);
  }
  
  private Course getCourse() {
    return Course.builder() .id("id").name("name").description("description").build();
  }
}
