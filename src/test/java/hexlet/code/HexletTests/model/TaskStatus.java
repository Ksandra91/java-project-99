package hexlet.code.HexletTests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatus {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
