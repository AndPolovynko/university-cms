package ua.foxminded.universitycms.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.service.UniversityClassService;
import ua.foxminded.universitycms.service.UserService;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Controller
@RequestMapping("student")
public class StudentController {
  private final UserService userService;
  private final UniversityClassService classService;

  @GetMapping
  public String getStudentProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    model.addAttribute("user", userService.getUserResponseByLogin(userDetails.getUsername()));
    return "student/student-profile-view";
  }

  @GetMapping("/timetable/today")
  public String getStudentDaySchedule(@AuthenticationPrincipal UserDetails userDetails, String requestedScheduleType,
      @RequestParam(required = false) String customRange, Model model, TimeZone timeZone) {
    model.addAttribute("classes",
        classService.getUniversityClassResponsesByDateRangeAndGroupName(
            OffsetDateTime.now(timeZone.toZoneId()).withHour(0).withMinute(0),
            OffsetDateTime.now(timeZone.toZoneId()).plusDays(1).withHour(0).withMinute(0),
            userDetails.getUsername()));
    return "student/student-day-schedule-view";
  }

  @GetMapping("/timetable/this-week")
  public String getStudentWeekSchedule(@AuthenticationPrincipal UserDetails userDetails, Model model,
      TimeZone timeZone) {
    model.addAttribute("classes",
        classService.getUniversityClassResponsesByDateRangeAndGroupName(
            OffsetDateTime.now(timeZone.toZoneId()).with(DayOfWeek.MONDAY),
            OffsetDateTime.now(timeZone.toZoneId()).with(DayOfWeek.SUNDAY),
            userDetails.getUsername()));
    return "student/student-week-schedule-view";
  }

  @GetMapping("/timetable/this-month")
  public String getStudentMonthSchedule(@AuthenticationPrincipal UserDetails userDetails, Model model,
      TimeZone timeZone) {
    model.addAttribute("classes",
        classService.getUniversityClassResponsesByDateRangeAndGroupName(
            OffsetDateTime.now(timeZone.toZoneId()).withDayOfMonth(1),
            OffsetDateTime.now(timeZone.toZoneId()).withDayOfMonth(LocalDate.now().lengthOfMonth()),
            userDetails.getUsername()));
    return "student/student-weeks-schedule-view";
  }

  @GetMapping("/timetable/custom-range")
  public String getStudentCustomSchedule(@AuthenticationPrincipal UserDetails userDetails,
      @RequestParam(required = false) String customRange, Model model, TimeZone timeZone) {
    model.addAttribute("classes",
        classService.getUniversityClassResponsesByDateRangeAndGroupName(customRange, timeZone,
            userDetails.getUsername()));
    return "student/student-weeks-schedule-view";
  }
}
