package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc

public class TaskControllerTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;


    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }


    @Test
    public void testIndex() throws Exception {
        User testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);

        TaskStatus testStatus = new TaskStatus();
        testStatus.setName("TestStatus");
        testStatus.setSlug("forTest");
        statusRepository.save(testStatus);

        Task testTask = new Task();
        testTask.setName("TaskName");
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testStatus);
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        TaskStatus testStatus = new TaskStatus();
        testStatus.setName("TestStatus2");
        testStatus.setSlug("forTest");
        statusRepository.save(testStatus);

        Task testTask = new Task();
        testTask.setName("TaskName2");
        // testTask.setAssignee(testUser);
        testTask.setTaskStatus(testStatus);
        taskRepository.save(testTask);

        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName())
        );

    }

    @Test
    public void testCreate() throws Exception {
        User testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);

        TaskStatus testStatus = new TaskStatus();
        testStatus.setName("TestStatus10");
        testStatus.setSlug("forTest1111");
        statusRepository.save(testStatus);

        TaskCreateDTO testTask = new TaskCreateDTO();
        testTask.setTitle("TaskName1111");
        testTask.setAssigneeId(testUser.getId());
        testTask.setStatus(testStatus.getName());

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTask))
                .with(token);

        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

        var body = result.getResponse().getContentAsString();
        var id = om.readTree(body).get("id").asLong();
        assertThat(taskRepository.findById(id)).isPresent();

        var taskFromRepo = taskRepository.findById(id).get();

        assertThat(taskFromRepo).isNotNull();
        assertThat(taskFromRepo.getName()).isEqualTo(testTask.getTitle());
    }

    @Test
    public void testUpdate() throws Exception {
        TaskStatus testStatus = new TaskStatus();
        testStatus.setName("TestStatus4");
        testStatus.setSlug("forTest");
        statusRepository.save(testStatus);

        Task testTask = new Task();
        testTask.setName("TaskName4");
        // testTask.setAssignee(testUser);
        testTask.setTaskStatus(testStatus);
        taskRepository.save(testTask);

        Map data = Map.of("title", "updateName");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo(("updateName"));

    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        TaskStatus testStatus = new TaskStatus();
        testStatus.setName("TestStatus5");
        testStatus.setSlug("forTest");
        statusRepository.save(testStatus);

        Task testTask = new Task();
        testTask.setName("TaskName5");
        // testTask.setAssignee(testUser);
        testTask.setTaskStatus(testStatus);
        taskRepository.save(testTask);

        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
