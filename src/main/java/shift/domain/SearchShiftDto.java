package shift.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class SearchShiftDto implements Serializable {
    private long fromStartTime;
    private long toEndTime;
}
