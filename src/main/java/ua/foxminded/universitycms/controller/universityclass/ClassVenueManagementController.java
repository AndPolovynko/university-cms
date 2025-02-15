package ua.foxminded.universitycms.controller.universityclass;

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
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;
import ua.foxminded.universitycms.service.UniversityClassVenueService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@PropertySource("classpath:view.properties")
@RequestMapping("admin/classes/venues")
public class ClassVenueManagementController {
  private final UniversityClassVenueService service;

  @GetMapping("/search")
  @ResponseBody
  public List<UniversityClassVenueResponse> searchVenues(@RequestParam(defaultValue = "") String keyword,
      @Value("${fastSearchLimit}") Integer fastSearchLimit) {
    return service.getClassVenueResponses(keyword, fastSearchLimit, "0").toList();
  }

  @GetMapping
  public String getVenues(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${venuesPerPage}") Integer pageSize) {
    
    Page<UniversityClassVenueResponse> venues = service.getClassVenueResponses(keyword, pageSize, pageNumber);
    model.addAttribute("venues", venues.getContent());
    model.addAttribute("currentPage", venues.getNumber() + 1);
    model.addAttribute("totalPages", venues.getTotalPages());
    model.addAttribute("keyword", keyword);
    return "admin/class-management/class-venue-management/class-venue-management-view";
  }

  @DeleteMapping("/{id}")
  public String deleteVenue(@PathVariable String id) {
    service.deleteById(id);
    return "redirect:/admin/classes/venues";
  }

  @GetMapping("/edit/{id}")
  public String getEditVenueView(Model model, @PathVariable String id) {
    UniversityClassVenueEditRequest venue = service.getClassVenueEditRequestById(id);
    model.addAttribute("venue", venue);
    return "admin/class-management/class-venue-management/class-venue-edit-view";
  }

  @PutMapping("/edit/{id}")
  public String updateVenue(@ModelAttribute("venue") UniversityClassVenueEditRequest request) {
    service.editFromRequest(request);
    return "redirect:/admin/classes/venues";
  }

  @GetMapping("/create")
  public String getCreateVenueView(Model model) {
    model.addAttribute("venue", UniversityClassVenueCreateRequest.builder().build());
    return "admin/class-management/class-venue-management/class-venue-create-view";
  }

  @PostMapping("/create")
  public String createVenue(@ModelAttribute("venue") UniversityClassVenueCreateRequest request) {
    service.saveFromRequest(request);
    return "redirect:/admin/classes/venues";
  }
}
