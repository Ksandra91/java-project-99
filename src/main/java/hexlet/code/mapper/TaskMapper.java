package hexlet.code.mapper;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusSlug")
    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "labelsId", target = "labels", qualifiedByName = "labels")
    public abstract Task map(TaskCreateDTO taskCreateDTO);


    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    public abstract TaskDTO map(Task task);


    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "labelsId", target = "labels", qualifiedByName = "labels")
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);


    @Named("statusSlug")
    public TaskStatus statusSlugToModel(String slug) {
        return statusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + slug));
    }

    @Named("labels")
    public List<Label> labelIdtoModel(List<Long> labelIds) {
        return labelIds == null ? new ArrayList<>() : labelRepository.findAllById(labelIds);
    }
}


