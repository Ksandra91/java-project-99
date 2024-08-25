package hexlet.code.dto.Task;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskParamsDTO {
    private String titleCont;
    private long assigneeId;
    private String status;
    private long labelId;
}
