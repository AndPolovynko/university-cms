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
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;
import ua.foxminded.universitycms.service.GroupService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@PropertySource("classpath:view.properties")
@RequestMapping("admin/groups")
public class GroupManagementController {
  private final GroupService service;

  @GetMapping("/search")
  @ResponseBody
  public List<GroupResponse> searchGroups(@RequestParam(defaultValue = "") String keyword,
      @Value("${fastSearchLimit}") Integer fastSearchLimit) {
    return service.getGroupResponses(keyword, fastSearchLimit, "0").toList();
  }

  @GetMapping
  public String getGroups(Model model, @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") String pageNumber, @Value("${groupsPerPage}") Integer pageSize) {
    Page<GroupResponse> groups = service.getGroupResponses(keyword, pageSize, pageNumber);
    model.addAttribute("groups", groups.getContent());
    model.addAttribute("currentPage", groups.getNumber() + 1);
    model.addAttribute("totalPages", groups.getTotalPages());
    model.addAttribute("keyword", keyword);
    return "admin/group-management/group-management-view";
  }

  @GetMapping("/{id}")
  public String getGroup(Model model, @PathVariable String id) {
    GroupResponse group = service.getGroupResponseById(id);
    model.addAttribute("group", group);
    return "admin/group-management/group-view";
  }

  @DeleteMapping("/{id}")
  public String deleteGroup(@PathVariable String id) {
    service.deleteById(id);
    return "redirect:/admin/groups";
  }

  @GetMapping("/edit/{id}")
  public String getEditGroupView(Model model, @PathVariable String id) {
    GroupEditRequest group = service.getGroupEditRequestById(id);
    model.addAttribute("group", group);
    return "admin/group-management/group-edit-view";
  }

  @PutMapping("/edit/{id}")
  public String updateGroup(@ModelAttribute("group") GroupEditRequest request) {
    service.editFromRequest(request);
    return "redirect:/admin/groups/" + request.getId();
  }

  @GetMapping("/create")
  public String getCreateGroupView(Model model) {
    model.addAttribute("group", GroupCreateRequest.builder().build());
    return "admin/group-management/group-create-view";
  }

  @PostMapping("/create")
  public String createGroup(@ModelAttribute("group") GroupCreateRequest request) {
    return "redirect:/admin/groups/" + service.saveFromRequest(request).getId();
  }
}
