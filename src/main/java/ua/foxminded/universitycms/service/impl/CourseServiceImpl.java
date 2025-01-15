package ua.foxminded.universitycms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;
import ua.foxminded.universitycms.mapper.CourseMapper;
import ua.foxminded.universitycms.repository.CourseRepository;
import ua.foxminded.universitycms.service.CourseService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class CourseServiceImpl implements CourseService {
  private final CourseRepository repo;
  private final CourseMapper mapper;

  @Override
  @Transactional
  public Course saveFromRequest(CourseCreateRequest request) {
    return repo.save(mapper.courseCreateRequestToCourse(request));
  }

  @Override
  @Transactional(readOnly = true)
  public CourseResponse getCourseResponseById(String id) {
    return mapper.courseToCourseResponse(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Course findByName(String name) {
    return repo.findByName(name)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with name=" + name + " doesn't exist."));
  }

  @Override
  @Transactional(readOnly = true)
  public CourseEditRequest getCourseEditRequestById(String id) {
    return mapper.courseToCourseEditRequest(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseResponse> getCourseResponses(String keyword, Integer itemsPerPage, Integer pageNumber) {
    if (keyword == null || keyword.isBlank()) {
      return repo.findAll(PageRequest.of(pageNumber, itemsPerPage, Sort.by("name")))
          .map(mapper::courseToCourseResponse);
    } else {
      return repo.findByNameContainingOrDescriptionContaining(keyword, keyword,
          PageRequest.of(pageNumber, itemsPerPage, Sort.by("name"))).map(mapper::courseToCourseResponse);
    }
  }

  @Override
  @Transactional
  public void editFromRequest(CourseEditRequest request) {
    repo.save(mapper.courseEditRequestToCourse(request));
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    repo.deleteById(id);
  }
}
