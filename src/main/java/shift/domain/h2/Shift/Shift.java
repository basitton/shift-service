package shift.domain.h2.Shift;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * Provides entity model for Shift data
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shift {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
