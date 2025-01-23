package ua.foxminded.universitycms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;
import ua.foxminded.universitycms.service.UserService;
import ua.foxminded.universitycms.service.exception.InvalidUserConfigurationException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@PropertySource("classpath:view.properties")
@RequestMapping("admin/users")
public class UserManagementController {
  private final UserService userService;

  @GetMapping("/search")
  @ResponseBody
  public List<UserResponse> searchUsers(@RequestParam(defaultValue = "") String keyword,
      @Value("${fastSearchLimit}") Integer fastSearchLimit) {
    return userService.getUserResponses(keyword, fastSearchLimit, 0).toList();
  }

  @GetMapping
  public String getUsers(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${usersPerPage}") Integer pageSize) {

    Integer pageNumberInt = Optional.of(pageNumber)
        .filter(param -> param.matches("\\d+"))
        .map(Integer::parseInt)
        .filter(num -> num > 0)
        .orElse(1);

    Page<UserResponse> users = userService.getUserResponses(keyword, pageSize, pageNumberInt - 1);
    model.addAttribute("users", users.getContent());
    model.addAttribute("currentPage", users.getNumber() + 1);
    model.addAttribute("totalPages", users.getTotalPages());
    model.addAttribute("keyword", keyword);
    return "admin/user-management/user-management-view";
  }

  @GetMapping("/{id}")
  public String getUser(Model model, @PathVariable String id) {
    UserResponse user = userService.getUserResponseById(id);
    model.addAttribute("user", user);
    return "admin/user-management/user-view";
  }

  @DeleteMapping("/{id}")
  public String deleteUser(@PathVariable String id) {
    userService.deleteById(id);
    return "redirect:/admin/users";
  }

  @GetMapping("/edit/{id}")
  public String getEditUserView(Model model, @PathVariable String id) {
    UserEditRequest user = userService.getUserEditRequestById(id);
    model.addAttribute("user", user);
    return "admin/user-management/user-edit-view";
  }

  @PutMapping("/edit/{type}/{id}")
  public String updateUser(@ModelAttribute("user") UserEditRequest request,
      @PathVariable("type") String requestedEditionType) {

    EditionType editionType = EditionType.fromString(requestedEditionType);

    if (editionType == EditionType.LOGIN_INFO) {
      userService.editLoginInfoFromRequest(request);
    } else if (editionType == EditionType.ROLES) {
      userService.editRolesAndDetailsFromRequest(request);
      return "redirect:/admin/users/edit/" + request.getId();
    } else {
      userService.editRolesAndDetailsFromRequest(request);
    }

    return "redirect:/admin/users/" + request.getId();
  }

  @GetMapping("/create")
  public String getCreateUserView(Model model) {
    model.addAttribute("user", UserCreateRequest.builder().build());
    return "admin/user-management/user-create-view";
  }

  @PostMapping("/create")
  public String createUser(Model model, @ModelAttribute("user") UserCreateRequest request) {
    return "redirect:/admin/users/" + userService.saveFromRequest(request).getId();
  }

  @ExceptionHandler(InvalidUserConfigurationException.class)
  public ModelAndView handleInvalidUserConfigurationException(InvalidUserConfigurationException ex) {
    ModelAndView modelAndView = new ModelAndView("error/custom-error");
    modelAndView.addObject("message", ex.getMessage());
    modelAndView.addObject("error", "Internal Server Error");
    modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return modelAndView;
  }
}
