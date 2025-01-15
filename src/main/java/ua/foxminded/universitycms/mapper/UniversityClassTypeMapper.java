package ua.foxminded.universitycms.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassType;
import ua.foxminded.universitycms.dto.UniversityClassTypeCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassTypeResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UniversityClassTypeMapper {
    UniversityClassTypeResponse classTypeToClassTypeResponse(UniversityClassType universityClassType);
    
    UniversityClassType classTypeCreateRequestToClassType(UniversityClassTypeCreateRequest request);
}