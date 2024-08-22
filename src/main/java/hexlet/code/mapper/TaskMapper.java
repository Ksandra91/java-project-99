package hexlet.code.mapper;


import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;

import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;


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

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus.slug")
    @Mapping(source = "assigneeId", target = "assignee.id")
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
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);


//    @Named("idToAssignee")
//    public User idToAssignee(Long assigneeId) {
//        return userRepository.findById(assigneeId).orElseThrow(
//                () -> new ResourceNotFoundException("User with id " + assigneeId + " not found"));
//    }
//
//    public TaskStatus statusToSlug(String slug) {
//        TaskStatus taskStatus = statusRepository.findBySlug(slug)
//                .orElseThrow(() -> new ResourceNotFoundException("No such status"));
//
//        return taskStatus;
//    }
//protected TaskStatus taskStatusFromSlug(String slug) {
//    return statusRepository.findBySlug(slug)
//            .orElseThrow(() -> new ResourceNotFoundException("Slug " + slug + " not found"));
//}
//
//    protected String statusSlugFromTaskStatus(TaskStatus status) {
//        return status.getSlug();
//    }
}



