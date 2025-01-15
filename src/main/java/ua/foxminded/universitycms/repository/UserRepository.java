package ua.foxminded.universitycms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByLogin(String login);
  
  Page<User> findByLoginContainingOrUserDetailsListLastNameContaining(String loginKeyword, String lastNameKeyword, Pageable pageable);
  
  @Modifying
  @Query("UPDATE User u SET u.login = ?1, u.password = ?2, u.email = ?3 WHERE u.id = ?4")
  void editLoginInfoById(String login, String password, String email, String id);
}
