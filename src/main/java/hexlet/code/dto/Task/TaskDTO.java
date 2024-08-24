package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
public class TaskDTO {
    private long id;

    private Integer index;

    private String createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;
    @NonNull
    private String title;

    private String content;
    @NonNull
    private String status;
    private Set<Long> labelsId = new HashSet<>();
}
