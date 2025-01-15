package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
  Optional<Group> findByName(String name);
  
  Page<Group> findByNameContaining(String keyword, Pageable pageable);
}
