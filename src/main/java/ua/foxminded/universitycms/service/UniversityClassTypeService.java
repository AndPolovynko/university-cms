package ua.foxminded.universitycms.service;

import java.util.List;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;

public interface UniversityClassTypeService {
    UniversityClassType saveFromRequest(UniversityClassTypeCreateRequest request);
    
    UniversityClassTypeResponse getUniversityClassTypeResponseById(String id);

    UniversityClassType findByName(String name);
    
    List<UniversityClassTypeResponse> getUniversityClassTypeResponses();

    void deleteById(String id);
}
