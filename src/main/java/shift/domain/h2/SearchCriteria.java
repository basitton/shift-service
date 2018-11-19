package shift.domain.h2;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
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
