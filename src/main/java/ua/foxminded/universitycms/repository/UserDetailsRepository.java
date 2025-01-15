package ua.foxminded.universitycms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.foxminded.universitycms.domain.userdetails.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, String> {

}
