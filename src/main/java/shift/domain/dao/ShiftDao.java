package shift.domain.dao;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import shift.domain.dto.ShiftDto;
import shift.exception.ShiftNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * persistence layer
 */
@Getter
@Setter
@Component
public class ShiftDao implements Dao<ShiftDto> {
    private static final Logger logger = LoggerFactory.getLogger(ShiftDao.class);
    // "fake" DB of shifts
    private List<ShiftDto> shifts = new ArrayList<>();

    public List<ShiftDto> getAll() {
        return shifts;
    }

    public ShiftDto get(int id) {
        return shifts.stream()
                .filter(shiftDto -> shiftDto.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new ShiftNotFoundException("Unable to find shift with id = " + id));
    }

    public void save(ShiftDto shift) {
        shifts.add(shift);
    }

    public void update(int id, ShiftDto updatedShiftDto) throws ShiftNotFoundException {
        shifts = shifts.stream()
                .map(shiftDto -> shiftDto.getId().equals(id) ? updatedShiftDto : shiftDto)
                .collect(Collectors.toList());
    }

    public void delete(int id) throws ShiftNotFoundException {
        shifts.remove(get(id));
    }
}
