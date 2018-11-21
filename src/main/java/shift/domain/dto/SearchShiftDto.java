package shift.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Range;

/**
 * Provides input data object for searching shifts
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SearchShiftDto {

    @Range(max = 23, message = "Shift hour can only be between 0-23")
    private int fromStartHour;

    @Range(max = 59, message = "Shift minute can only be between 0-59")
    private int fromStartMinute;

    @Range(max = 23, message = "Shift hour can only be between 0-23")
    private int toEndHour;

    @Range(max = 59, message = "Shift minute can only be between 0-59")
    private int toEndMinute;
}
