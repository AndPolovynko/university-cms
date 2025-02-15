package ua.foxminded.universitycms.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.dto.CourseCreateRequest;
import ua.foxminded.universitycms.dto.CourseEditRequest;
import ua.foxminded.universitycms.dto.CourseResponse;
import ua.foxminded.universitycms.service.CourseService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@PropertySource("classpath:view.properties")
@RequestMapping("admin/courses")
public class CourseManagementController {
  private final CourseService service;
  
  @GetMapping("/search")
  @ResponseBody
  public List<CourseResponse> searchCourses(@RequestParam(defaultValue = "") String keyword,
      @Value("${fastSearchLimit}") Integer fastSearchLimit) {
    return service.getCourseResponses(keyword, fastSearchLimit, "0").toList();
  }
  
  @GetMapping
  public String getCourses(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${coursesPerPage}") Integer pageSize) {
    Page<CourseResponse> courses = service.getCourseResponses(keyword, pageSize, pageNumber);
    model.addAttribute("courses", courses.getContent());
    model.addAttribute("currentPage", courses.getNumber() + 1);
    model.addAttribute("totalPages", courses.getTotalPages());
    model.addAttribute("keyword", keyword);
    return "admin/course-management/course-management-view";
  }

  @GetMapping("/{id}")
  public String getCourse(Model model, @PathVariable String id) {
    CourseResponse course = service.getCourseResponseById(id);
    model.addAttribute("course", course);
    return "admin/course-management/course-view";
  }

  @DeleteMapping("/{id}")
  public String deleteCourse(@PathVariable String id) {
    service.deleteById(id);
    return "redirect:/admin/courses";
  }

  @GetMapping("/edit/{id}")
  public String getEditCourseView(Model model, @PathVariable String id) {
    CourseEditRequest course = service.getCourseEditRequestById(id);
    model.addAttribute("course", course);
    return "admin/course-management/course-edit-view";
  }

  @PutMapping("/edit/{id}")
  public String updateCourse(@ModelAttribute("course") CourseEditRequest request) {
    service.editFromRequest(request);
    return "redirect:/admin/courses/" + request.getId();
  }

  @GetMapping("/create")
  public String getCreateCourseView(Model model) {
    model.addAttribute("course", CourseCreateRequest.builder().build());
    return "admin/course-management/course-create-view";
  }

  @PostMapping("/create")
  public String createCourse(@ModelAttribute("course") CourseCreateRequest request) {
    return "redirect:/admin/courses/" + service.saveFromRequest(request).getId();
  }
}
