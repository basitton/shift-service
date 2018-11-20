package shift.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Provides returned data object for a shift
 */
@Builder
@Getter
@Setter
@Data
public class ResultShiftDto {
    private Long id;
    private String user;
    private String startTime;
    private String endTime;
}
