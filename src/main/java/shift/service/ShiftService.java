package shift.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shift.domain.Shift;
import shift.domain.dao.ShiftDao;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.exception.ShiftIllegalArgumentException;
import shift.exception.ShiftNotFoundException;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    // a key would usually be managed by a DB
    private int key = 0;

    private ShiftDao shiftDao;

    @Autowired
    public ShiftService(ShiftDao shiftDao) {
        this.shiftDao = shiftDao;
    }

    public Shift createShift(@NotNull ShiftDto shiftDto) {
        validateShiftTimes(shiftDto);

        shiftDto.setId(key);
        // increment identifier for each shift (would usually be managed by a DB)
        key++;

        // persist shift into "database"
        shiftDao.save(shiftDto);

        // build the result to return
        return buildResultShift(shiftDto);
    }

    public List<Shift> searchShifts(@NotNull SearchShiftDto searchShiftDto) {

        return shiftDao.getAll().stream()
                .filter(shiftDto -> isShiftWithinSearch(shiftDto, searchShiftDto))
                .sorted(Comparator.comparing(ShiftDto::getStartHour).thenComparing(ShiftDto::getStartMinute))
                .map(this::buildResultShift)
                .collect(Collectors.toList());
    }

    public Shift getShift(@NotNull int shiftId) throws ShiftNotFoundException {
        return buildResultShift(shiftDao.get(shiftId));
    }

    public Shift updateShift(@NotNull int shiftId, @NotNull ShiftDto shiftDto) throws ShiftNotFoundException {
        validateShiftTimes(shiftDto);
        shiftDao.update(shiftId, shiftDto);
        // return updated shift result
        return buildResultShift(shiftDao.get(shiftId));
    }

    public void deleteShift(@NotNull int shiftId) throws ShiftNotFoundException {
        shiftDao.delete(shiftId);
    }

    private void validateShiftTimes(ShiftDto shiftDto) {
        LocalTime startTime = convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute());
        LocalTime endTime = convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute());

        if (startTime.equals(endTime)) {
            throw new ShiftIllegalArgumentException("Shifts must have a different start and end time range.");
        }
        if (endTime.isBefore(startTime)) {
            throw new ShiftIllegalArgumentException("A shift's end time cannot be before a shift's start time.");
        }

        // validate newly created shift does not overlap with an existing shift
        boolean shiftExistsForTimeFrame = shiftDao.getAll().stream()
                .anyMatch(existingShiftDto -> isShiftOverlappingWithAnother(shiftDto, existingShiftDto));

        if(shiftExistsForTimeFrame) {
            throw new ShiftIllegalArgumentException("This shift overlaps with an existing shift.");
        }
    }

    private Shift buildResultShift(ShiftDto shiftDto) {
        return Shift.builder()
                .id(String.valueOf(shiftDto.getId()))
                .startTime(convertToLocalTimeAndFormat(shiftDto.getStartHour(), shiftDto.getStartMinute()))
                .endTime(convertToLocalTimeAndFormat(shiftDto.getEndHour(), shiftDto.getEndMinute()))
                .build();
    }

    private boolean isShiftOverlappingWithAnother(ShiftDto newShiftDto, ShiftDto existingShiftDto) {
        LocalTime newStartTime = convertToTime(newShiftDto.getStartHour(), newShiftDto.getStartMinute());
        LocalTime newEndTime = convertToTime(newShiftDto.getEndHour(), newShiftDto.getEndMinute());

        LocalTime existingStartTime = convertToTime(existingShiftDto.getStartHour(), existingShiftDto.getStartMinute());
        LocalTime existingEndTime = convertToTime(existingShiftDto.getEndHour(), existingShiftDto.getEndMinute());

        return newStartTime.equals(existingStartTime) ||
                newEndTime.equals(existingEndTime) ||
                isTimeWithinRange(newStartTime, existingStartTime, existingEndTime) ||
                isTimeWithinRange(newEndTime, existingStartTime, existingEndTime) ||
                (newStartTime.isBefore(existingStartTime) && newEndTime.isAfter(existingEndTime));
    }

    private boolean isShiftWithinSearch(ShiftDto existingShiftDto, SearchShiftDto searchShiftDto) {
        LocalTime shiftStartTime = convertToTime(existingShiftDto.getStartHour(), existingShiftDto.getStartMinute());
        LocalTime shiftEndTime = convertToTime(existingShiftDto.getEndHour(), existingShiftDto.getEndMinute());

        LocalTime searchStartTime = convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        return (shiftStartTime.equals(searchStartTime) || shiftStartTime.isAfter(searchStartTime)) &&
                (shiftEndTime.equals(searchEndTime) || shiftEndTime.isBefore(searchEndTime));
    }

    private boolean isTimeWithinRange(LocalTime time, LocalTime rangeStartTime, LocalTime rangeEndTime) {
        return time.isAfter(rangeStartTime) && time.isBefore(rangeEndTime);
    }

    private LocalTime convertToTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    private String convertToLocalTimeAndFormat(int hour, int minute) {
        return convertToTime(hour, minute).format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
