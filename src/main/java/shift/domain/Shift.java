package shift.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Shift {

    private long id;
    private long startTime;
    private long endTime;
}
