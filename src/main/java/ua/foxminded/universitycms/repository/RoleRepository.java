package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
  Optional<Role> findByName(String name);
}
