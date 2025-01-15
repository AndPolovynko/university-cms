package ua.foxminded.universitycms.controller.universityclass;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;
import ua.foxminded.universitycms.service.UniversityClassTypeService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@RequestMapping("admin/classes/types")
public class ClassTypeManagementController {
  private final UniversityClassTypeService service;
  
  @GetMapping
  public String getUniversityClassTypes(Model model) {
    List<UniversityClassTypeResponse> types = service.getUniversityClassTypeResponses();
    model.addAttribute("types", types);
    return "admin/class-management/class-type-management/class-type-management-view.html";
  }
  
  @DeleteMapping("/{id}")
  public String deleteUniversityClassType(@PathVariable String id) {
    service.deleteById(id);
    return "redirect:/admin/classes/types";
  }

  @GetMapping("/create")
  public String getCreateUniversityClassTypeView(Model model) {
    model.addAttribute("type", UniversityClassTypeCreateRequest.builder().build());
    return "admin/class-management/class-type-management/class-type-create-view";
  }

  @PostMapping("/create")
  public String createUniversityClassType(@ModelAttribute("class") UniversityClassTypeCreateRequest request) {
    service.saveFromRequest(request);
    return "redirect:/admin/classes/types";
  }
}
