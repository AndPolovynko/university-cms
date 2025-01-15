package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;

@Repository
public interface UniversityClassVenueRepository extends JpaRepository<UniversityClassVenue, String> {
  Optional<UniversityClassVenue> findByName(String name);
  
  Page<UniversityClassVenue> findByNameContaining(String keyword, Pageable pageable);
}
