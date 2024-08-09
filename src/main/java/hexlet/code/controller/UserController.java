package hexlet.code.controller;

import java.util.List;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper postMapper;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> index() {
        var users = repository.findAll();
        var result = users.stream()
                .map(postMapper::map)
                .toList();

        return result;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userData) {
        var user = postMapper.map(userData);
        repository.save(user);
        var userDTO = postMapper.map(user);
        return userDTO;
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var userDTO = postMapper.map(user);
        return userDTO;
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@RequestBody @Valid UserUpdateDTO userData, @PathVariable Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        postMapper.update(userData, user);
        repository.save(user);
        var userDTO = postMapper.map(user);
        return userDTO;
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
