package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusCreateDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String slug;

}
