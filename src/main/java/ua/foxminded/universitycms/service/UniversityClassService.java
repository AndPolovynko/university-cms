package ua.foxminded.universitycms.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.data.domain.Page;

import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.dto.UniversityClassCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassResponse;

public interface UniversityClassService {
  UniversityClass saveFromRequest(UniversityClassCreateRequest request);

  Iterable<UniversityClass> saveFromRequest(UniversityClassCreateRequest request, String repeat,
      String repeatUntil);

  UniversityClassResponse getUniversityClassResponseById(String id);

  UniversityClassEditRequest getUniversityClassEditRequestById(String id);

  Page<UniversityClassResponse> getUniversityClassResponses(String keyword, Integer itemsPerPage, String pageNumber);

  Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndGroupName(
      OffsetDateTime startDate, OffsetDateTime endDate, String studentLogin);

  Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndGroupName(
      String customRange, TimeZone timeZone, String studentLogin);

  Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndTeacherLogin(
      OffsetDateTime startDate, OffsetDateTime endDate, String teacherLogin);

  Map<String, Map<String, List<UniversityClassResponse>>> getUniversityClassResponsesByDateRangeAndTeacherLogin(
      String customRange, TimeZone timeZone, String teacherLogin);

  void editFromRequest(UniversityClassEditRequest request);

  void deleteById(String id);
}
