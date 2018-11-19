package shift.domain.h2.Shift;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Shift {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @NonNull
    private LocalTime startTime;

    @NonNull
    private LocalTime endTime;
}
