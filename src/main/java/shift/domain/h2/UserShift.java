package shift.domain.h2;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserShift {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private LocalTime startTime;

    @NonNull
    private LocalTime endTime;
}
