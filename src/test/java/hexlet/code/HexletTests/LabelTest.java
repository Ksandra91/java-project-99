package hexlet.code.HexletTests;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext;

import hexlet.code.HexletTests.util.TestUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.HexletTests.util.ModelGenerator;
import hexlet.code.HexletTests.model.Label;
import hexlet.code.AppApplication;

@Order(2)
@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LabelTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private final ModelGenerator modelGenerator = new ModelGenerator();

    private Label testLabel;


    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testLabel = Instancio.of(modelGenerator.getLabelModel())
                .create();
    }

    @Order(1)
    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();

        var request = post("/api/labels").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(data.getName()),
                v -> v.node("createdAt").isPresent());

    }

    @Order(2)
    @Test
    public void testIndex() throws Exception {
        TestUtils.saveLabel(mockMvc, testLabel);

        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Order(3)
    @Test
    public void testShow() throws Exception {
        TestUtils.saveLabel(mockMvc, testLabel);

        var label = TestUtils.getLabelByName(mockMvc, testLabel.getName());

        var request = get("/api/labels/{id}", label.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Order(4)
    @Test
    public void testUpdate() throws Exception {
        TestUtils.saveLabel(mockMvc, testLabel);
        var label = TestUtils.getLabelByName(mockMvc, testLabel.getName());

        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();
        var request = put("/api/labels/{id}", label.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.getName())
        );

        var actualLabel = TestUtils.getLabelByName(mockMvc, data.getName());

        assertNotNull(actualLabel);
        assertEquals(data.getName(), actualLabel.getName());
    }

    @Order(5)
    @Test
    public void testDelete() throws Exception {
        TestUtils.saveLabel(mockMvc, testLabel);
        var label = TestUtils.getLabelByName(mockMvc, testLabel.getName());

        mockMvc.perform(delete("/api/labels/{id}", label.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        assertNull(TestUtils.getLabelByName(mockMvc, testLabel.getName()));
    }
}
