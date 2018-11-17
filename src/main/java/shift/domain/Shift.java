package shift.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class Shift {

    @NotEmpty
    private String id;
    @NotEmpty
    private String startTime;
    @NotEmpty
    private String endTime;
}
