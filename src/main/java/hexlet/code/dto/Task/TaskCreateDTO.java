package hexlet.code.dto.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDTO {


    private Integer index;

    private long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private String status;

    private Set<Long> taskLabelIds;

}
