package shift.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Provides input data object for managing shifts
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ShiftDto {

    private Integer id;

    private String username;

    @Range(max = 23, message = "Shift hour can be between 0-23")
    @NotNull
    private Integer startHour;

    @Range(max = 59, message = "Shift minute can be between 0-59")
    @NotNull
    private Integer startMinute;

    @Range(max = 23, message = "Shift hour can be between 0-23")
    @NotNull
    private Integer endHour;

    @Range(max = 59, message = "Shift minute can be between 0-59")
    @NotNull
    private Integer endMinute;
}
