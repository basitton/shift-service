package shift.domain.dao;

import lombok.Getter;
import lombok.Setter;
import shift.domain.SearchShiftDto;
import shift.domain.Shift;
import shift.domain.dto.ShiftDto;

import java.util.List;

@Getter
@Setter
public class ShiftDao {

    public Shift get(long id) {
        return null;
    }

    public List<Shift> searchShifts(SearchShiftDto searchShiftDto) {
        return null;
    }

    public void create(ShiftDto params) {

    }

    public void update(long id, ShiftDto params) {

    }

    public void delete(long id) {

    }
}
