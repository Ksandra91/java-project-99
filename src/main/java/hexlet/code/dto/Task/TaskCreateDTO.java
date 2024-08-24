package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskCreateDTO {

    @NotNull
    private String title;
    private int index;
    private String content;
    @NotNull
    private String status;
    @JsonProperty("assignee_id")
    private long assigneeId;
    private List<Long> labelsId = new ArrayList<>();

}
