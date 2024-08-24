package hexlet.code.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labeles")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Label implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    @NotNull
    @Size(min = 3, max = 1000)
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(mappedBy = "labels", cascade = CascadeType.MERGE)
    private Set<Task> tasks = new HashSet<>();


    public void addTask(Task task) {
        tasks.add(task);
        task.getLabels().add(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.getLabels().remove(this);
    }
}
