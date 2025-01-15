package ua.foxminded.universitycms.controller.universityclass;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;
import ua.foxminded.universitycms.service.UniversityClassService;
import ua.foxminded.universitycms.service.UniversityClassTypeService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@PropertySource("classpath:view.properties")
@RequestMapping("admin/classes")
public class ClassManagementController {
  private final UniversityClassService service;
  private final UniversityClassTypeService typeService;
  
  @GetMapping
  public String getUniversityClasses(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${classesPerPage}") Integer pageSize) {
    
    Integer pageNumberInt = Optional.of(pageNumber)
        .filter(param -> param.matches("\\d+"))
        .map(Integer::parseInt)
        .filter(num -> num > 0)
        .orElse(1);
    
    Page<UniversityClassResponse> classes = service.getUniversityClassResponses(keyword, pageSize, pageNumberInt - 1);
    model.addAttribute("classes", classes.getContent());
    model.addAttribute("currentPage", classes.getNumber() + 1);
    model.addAttribute("totalPages", classes.getTotalPages());
    model.addAttribute("keyword", keyword);
    return "admin/class-management/class-management-view";
  }

  @GetMapping("/{id}")
  public String getUniversityClass(Model model, @PathVariable String id) {
    UniversityClassResponse universityClass = service.getUniversityClassResponseById(id);
    model.addAttribute("class", universityClass);
    return "admin/class-management/class-view";
  }

  @DeleteMapping("/{id}")
  public String deleteUniversityClass(@PathVariable String id) {
    service.deleteById(id);
    return "redirect:/admin/classes";
  }

  @GetMapping("/edit/{id}")
  public String getEditUniversityClassView(Model model, @PathVariable String id) {
    UniversityClassEditRequest universityClass = service.getUniversityClassEditRequestById(id);
    model.addAttribute("class", universityClass);
    model.addAttribute("types", typeService.getUniversityClassTypeResponses());
    return "admin/class-management/class-edit-view";
  }

  @PutMapping("/edit/{id}")
  public String updateUniversityClass(@ModelAttribute("class") UniversityClassEditRequest request) {
    service.editFromRequest(request);
    return "redirect:/admin/classes/" + request.getId();
  }

  @GetMapping("/create")
  public String getCreateUniversityClassView(Model model) {
    model.addAttribute("class", UniversityClassCreateRequest.builder().build());
    model.addAttribute("types", typeService.getUniversityClassTypeResponses());
    return "admin/class-management/class-create-view";
  }

  @PostMapping("/create")
  public String createUniversityClass(@ModelAttribute("class") UniversityClassCreateRequest request) {
    return "redirect:/admin/classes/" + service.saveFromRequest(request).getId();
  }
}
