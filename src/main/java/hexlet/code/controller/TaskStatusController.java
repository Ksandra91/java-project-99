package hexlet.code.controller;

import java.util.List;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import hexlet.code.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private final TaskStatusRepository repository;
    private final TaskStatusMapper statusMapper;

    @GetMapping("")
    ResponseEntity<List<TaskStatusDTO>> index() {
        var statuses = repository.findAll();
        var result = statuses.stream()
                .map(statusMapper::map)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(result);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO statusData) {
        var status = statusMapper.map(statusData);
        repository.save(status);
        var statusDTO = statusMapper.map(status);
        return statusDTO;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var statusDTO = statusMapper.map(status);
        return statusDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@RequestBody @Valid TaskStatusUpdateDTO statusData, @PathVariable Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        statusMapper.update(statusData, status);
        repository.save(status);
        var statusDTO = statusMapper.map(status);
        return statusDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
