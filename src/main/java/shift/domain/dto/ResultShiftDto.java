package shift.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResultShiftDto {
    private Long id;
    private String user;
    private String startTime;
    private String endTime;
}
