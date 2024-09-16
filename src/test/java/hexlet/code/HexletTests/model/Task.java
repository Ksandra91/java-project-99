package hexlet.code.HexletTests.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Long id;
    @JsonProperty("taskLabelIds")
    private Set<Long> labels;
    private Integer index;
    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private Long assignee;

    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @JsonProperty("status")
    private String taskStatus;
}
