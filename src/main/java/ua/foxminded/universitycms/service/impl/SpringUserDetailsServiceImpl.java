package ua.foxminded.universitycms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.UserPrincipal;
import ua.foxminded.universitycms.repository.UserRepository;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SpringUserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository repo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = repo.findByLogin(username)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("User with login=" + username + " doesn't exist."));
    return new UserPrincipal(user);
  }
}
