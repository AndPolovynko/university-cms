package ua.foxminded.universitycms.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.universityclass.UniversityClass;

@Repository
public interface UniversityClassRepository extends JpaRepository<UniversityClass, String> {
  Page<UniversityClass> findByTypeNameContainingOrVenueNameContainingOrCourseNameContaining(String typeKeyword,
      String venueKeyword, String courseKeyword, Pageable pageable);

  List<UniversityClass> findByDateTimeBetweenAndGroupsNameOrderByDateTimeAsc(OffsetDateTime startDate,
      OffsetDateTime endDate, String groupName);
  
  List<UniversityClass> findByDateTimeBetweenAndTeachersLoginOrderByDateTimeAsc(OffsetDateTime startDate,
      OffsetDateTime endDate, String teacherLogin);
}
