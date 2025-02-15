package ua.foxminded.universitycms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;
import ua.foxminded.universitycms.mapper.GroupMapper;
import ua.foxminded.universitycms.repository.GroupRepository;
import ua.foxminded.universitycms.service.GroupService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;
import ua.foxminded.universitycms.service.util.PageNumberParser;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class GroupServiceImpl implements GroupService {
  private final GroupRepository repo;
  private final GroupMapper mapper;

  @Override
  @Transactional
  public Group saveFromRequest(GroupCreateRequest request) {
    return repo.save(mapper.groupCreateRequestToGroup(request));
  }

  @Override
  @Transactional(readOnly = true)
  public GroupResponse getGroupResponseById(String id) {
    return mapper.groupToGroupResponse(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Group findByName(String name) {
    return repo.findByName(name)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with name=" + name + " doesn't exist."));
  }

  @Override
  @Transactional(readOnly = true)
  public GroupEditRequest getGroupEditRequestById(String id) {
    return mapper.groupToGroupEditRequest(repo.findById(id)
        .orElseThrow(() -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupResponse> getGroupResponses(String keyword, Integer itemsPerPage, String pageNumber) {
    if (keyword == null || keyword.isBlank()) {
      return repo.findAll(PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("name")))
          .map(mapper::groupToGroupResponse);
    } else {
      return repo
          .findByNameContaining(keyword,
              PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("name")))
          .map(mapper::groupToGroupResponse);
    }
  }

  @Override
  @Transactional
  public void editFromRequest(GroupEditRequest request) {
    repo.save(mapper.groupEditRequestToGroup(request));
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    repo.deleteById(id);
  }
}
