package ua.foxminded.universitycms.controller.universityclass;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;
import ua.foxminded.universitycms.service.UniversityClassVenueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    return service.getClassVenueResponses(keyword, fastSearchLimit, 0).toList();
  }

  @GetMapping
  public String getVenues(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${venuesPerPage}") Integer pageSize) {
    
    Integer pageNumberInt = Optional.of(pageNumber)
        .filter(param -> param.matches("\\d+"))
        .map(Integer::parseInt)
        .filter(num -> num > 0)
        .orElse(1);
    
    Page<UniversityClassVenueResponse> venues = service.getClassVenueResponses(keyword, pageSize, pageNumberInt - 1);
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
