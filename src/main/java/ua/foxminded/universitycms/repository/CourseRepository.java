package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
  Optional<Course> findByName(String name);
  
  Page<Course> findByNameContainingOrDescriptionContaining(String nameKeyword, String descriptionKeyword, Pageable pageable);
}
