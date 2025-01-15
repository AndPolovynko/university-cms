package ua.foxminded.universitycms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.service.UserService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@RequestMapping("admin")
public class AdminController {
  private final UserService userService;
  
  @GetMapping
  public String getAdminProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
      model.addAttribute("user", userService.getUserResponseByLogin(userDetails.getUsername()));
      return "admin/admin-profile-view";
  }
}
