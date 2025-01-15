package ua.foxminded.universitycms.service;

import org.springframework.data.domain.Page;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;

public interface UniversityClassVenueService {
  UniversityClassVenue saveFromRequest(UniversityClassVenueCreateRequest request);

  UniversityClassVenueResponse getClassVenueResponseById(String id);

  UniversityClassVenue findByName(String name);

  UniversityClassVenueEditRequest getClassVenueEditRequestById(String id);

  Page<UniversityClassVenueResponse> getClassVenueResponses(String keyword, Integer itemsPerPage, Integer pageNumber);

  void editFromRequest(UniversityClassVenueEditRequest request);

  void deleteById(String id);
}
