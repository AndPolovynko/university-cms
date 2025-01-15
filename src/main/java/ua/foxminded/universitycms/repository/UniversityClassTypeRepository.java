package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;

@Repository
public interface UniversityClassTypeRepository extends JpaRepository<UniversityClassType, String> {
  Optional<UniversityClassType> findByName(String name);
}
