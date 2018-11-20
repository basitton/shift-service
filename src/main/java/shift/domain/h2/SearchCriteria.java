package shift.domain.h2;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

/**
 * Provides search criteria data for building a {@link Specification}
 */
@Getter
@Setter
@Data
public class SearchCriteria {
    public SearchCriteria(String key, String operation, LocalTime time) {
        this.key = key;
        this.operation = operation;
        this.time = time;
    }

    public SearchCriteria(String key, String operation, String username) {
        this.key = key;
        this.operation = operation;
        this.username = username;
    }

    public SearchCriteria(String key, String operation, long id) {
        this.key = key;
        this.operation = operation;
        this.id = id;
    }

    private String key;
    private String operation;
    private LocalTime time;
    private String username;
    private long id;
}
