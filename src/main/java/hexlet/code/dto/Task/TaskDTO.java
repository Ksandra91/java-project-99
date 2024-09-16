package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private long id;

    private Integer index;

    private String createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;

    private String title;

    private String content;

    private String status;

    private Set<Long> taskLabelIds;
}
