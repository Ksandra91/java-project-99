package hexlet.code.dto.Label;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {
    @NotNull
    private JsonNullable<String> name;
}
