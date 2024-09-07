package hexlet.code.controller;

import java.util.List;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
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
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelRepository repository;
    private final LabelMapper labelMapper;

    @GetMapping("")
    ResponseEntity<List<LabelDTO>> index() {
        var labels = repository.findAll();
        var result = labels.stream()
                .map(labelMapper::map)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(result);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        repository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelDTO show(@PathVariable Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelDTO update(@RequestBody @Valid LabelUpdateDTO labelData, @PathVariable Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        labelMapper.update(labelData, label);
        repository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
