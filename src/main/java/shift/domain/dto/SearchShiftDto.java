package shift.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
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
