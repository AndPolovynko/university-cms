package ua.foxminded.universitycms.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestController {

  @GetMapping
  public String getWelcomView() {
    return getViewBasedOnUserAuthorities(SecurityContextHolder.getContext().getAuthentication(),
        "guest/guest-welocme-view");
  }

  @GetMapping("/login")
  public String getLoginView() {
    return getViewBasedOnUserAuthorities(SecurityContextHolder.getContext().getAuthentication(),
        "guest/guest-login-view");
  }

  private String getViewBasedOnUserAuthorities(Authentication authentication, String guestViewName) {
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
        return "redirect:/admin";
      }
      if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
        return "redirect:/teacher";
      }
      if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
        return "redirect:/student";
      }
    }
    return guestViewName;
  }
}
