package ua.foxminded.universitycms.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import ua.foxminded.universitycms.domain.universityclass.UniversityClassVenue;
import ua.foxminded.universitycms.dto.UniversityClassVenueCreateRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueEditRequest;
import ua.foxminded.universitycms.dto.UniversityClassVenueResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UniversityClassVenueMapper {
    UniversityClassVenueResponse classVenueToClassVenueResponse(UniversityClassVenue venue);
    
    UniversityClassVenueEditRequest classVenueToClassVenueEditRequest(UniversityClassVenue venue);
    
    UniversityClassVenue classVenueCreateRequestToClassVenue(UniversityClassVenueCreateRequest request);
    
    UniversityClassVenue classVenueEditRequestToClassVenue(UniversityClassVenueEditRequest request);
}
