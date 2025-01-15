package ua.foxminded.universitycms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;
import ua.foxminded.universitycms.mapper.UniversityClassVenueMapper;
import ua.foxminded.universitycms.repository.UniversityClassVenueRepository;
import ua.foxminded.universitycms.service.UniversityClassVenueService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UniversityClassVenueServiceImpl implements UniversityClassVenueService {
  private final UniversityClassVenueRepository repo;
  private final UniversityClassVenueMapper mapper;

  @Override
  @Transactional
  public UniversityClassVenue saveFromRequest(UniversityClassVenueCreateRequest request) {
    return repo.save(mapper.classVenueCreateRequestToClassVenue(request));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassVenueResponse getClassVenueResponseById(String id) {
    return mapper.classVenueToClassVenueResponse(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassVenue findByName(String name) {
    return repo.findByName(name)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with name=" + name + " doesn't exist."));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassVenueEditRequest getClassVenueEditRequestById(String id) {
    return mapper.classVenueToClassVenueEditRequest(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityClassVenueResponse> getClassVenueResponses(String keyword, Integer itemsPerPage,
      Integer pageNumber) {
    if (keyword == null || keyword.isBlank()) {
      return repo.findAll(PageRequest.of(pageNumber, itemsPerPage, Sort.by("name")))
          .map(mapper::classVenueToClassVenueResponse);
    } else {
      return repo.findByNameContaining(keyword, PageRequest.of(pageNumber, itemsPerPage, Sort.by("name")))
          .map(mapper::classVenueToClassVenueResponse);
    }
  }

  @Override
  @Transactional
  public void editFromRequest(UniversityClassVenueEditRequest request) {
    repo.save(mapper.classVenueEditRequestToClassVenue(request));
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    repo.deleteById(id);
  }
}
