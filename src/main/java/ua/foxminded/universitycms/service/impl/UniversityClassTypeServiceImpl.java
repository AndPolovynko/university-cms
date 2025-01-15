package ua.foxminded.universitycms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;
import ua.foxminded.universitycms.mapper.UniversityClassTypeMapper;
import ua.foxminded.universitycms.repository.UniversityClassTypeRepository;
import ua.foxminded.universitycms.service.UniversityClassTypeService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UniversityClassTypeServiceImpl implements UniversityClassTypeService {
  private final UniversityClassTypeRepository repo;
  private final UniversityClassTypeMapper mapper;

  @Override
  @Transactional
  public UniversityClassType saveFromRequest(UniversityClassTypeCreateRequest request) {
    return repo.save(mapper.classTypeCreateRequestToClassType(request));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassTypeResponse getUniversityClassTypeResponseById(String id) {
    return mapper.classTypeToClassTypeResponse(repo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("UniversityClassType with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityClassType findByName(String name) {
    return repo.findByName(name).orElseThrow(
        () -> new EntityNotFoundRuntimeException("UniversityClassType with name=" + name + " doesn't exist."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UniversityClassTypeResponse> getUniversityClassTypeResponses() {
    List<UniversityClassTypeResponse> responses = new ArrayList<UniversityClassTypeResponse>();
    repo.findAll().forEach(type -> {
      responses.add(mapper.classTypeToClassTypeResponse(type));
    });
    return responses;
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    repo.deleteById(id);
  }
}
