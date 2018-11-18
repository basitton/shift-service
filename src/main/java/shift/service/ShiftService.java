package shift.service;

import org.springframework.stereotype.Service;
import shift.domain.Shift;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.UserShift;
import shift.domain.h2.UserShiftSpecification;
import shift.exception.ShiftIllegalArgumentException;
import shift.exception.ShiftNotFoundException;
import shift.repository.UserShiftDao;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {
    private static final String USERNAME_KEY = "username";
    private static final String START_TIME_KEY = "startTime";
    private static final String END_TIME_KEY = "endTime";

    private UserShiftDao userShiftDao;

    public ShiftService(UserShiftDao userShiftDao) {
        this.userShiftDao = userShiftDao;
    }

    public Shift createShift(@NotNull ShiftDto shiftDto) {
        validateShiftTimes(shiftDto);

        UserShift userShift = translateShift(shiftDto);

        // persist shift into "database"
        userShiftDao.save(userShift);

        // build the result to return
        return buildResultShift(userShift);
    }

    public List<Shift> searchShifts(@NotNull SearchShiftDto searchShiftDto) {
        LocalTime searchStartTime = convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        UserShiftSpecification userSpec = getSpecification(getSearchCriteriaForString(USERNAME_KEY, ":", "Test"));
        UserShiftSpecification startSpec = getSpecification(getSearchCriteriaForTime(START_TIME_KEY, ">:", searchStartTime));
        UserShiftSpecification endSpec = getSpecification(getSearchCriteriaForTime(END_TIME_KEY, "<:", searchEndTime));

        return userShiftDao.getAll(userSpec, startSpec, endSpec).stream()
                .map(this::buildResultShift)
                .collect(Collectors.toList());
    }

    public Shift getShift(@NotNull long shiftId) throws ShiftNotFoundException {
        return buildResultShift(userShiftDao.get(shiftId));
    }

    public Shift updateShift(@NotNull long shiftId, @NotNull ShiftDto shiftDto) throws ShiftNotFoundException {
        validateShiftTimes(shiftDto);
        userShiftDao.update(shiftId, translateShift(shiftDto));
        // return updated shift result
        return buildResultShift(userShiftDao.get(shiftId));
    }

    public void deleteShift(@NotNull long shiftId) throws ShiftNotFoundException {
        userShiftDao.delete(shiftId);
    }

    private void validateShiftTimes(ShiftDto shiftDto) {
        int startHour = shiftDto.getStartHour();
        int startMinute = shiftDto.getStartMinute();
        int endHour = shiftDto.getEndHour();
        int endMinute = shiftDto.getEndMinute();

        LocalTime startTime = convertToTime(startHour, startMinute);
        LocalTime endTime = convertToTime(endHour, endMinute);

        if (startTime.equals(endTime)) {
            throw new ShiftIllegalArgumentException("Shifts must have a different start and end time range.");
        }
        if (endTime.isBefore(startTime)) {
            throw new ShiftIllegalArgumentException("A shift's end time cannot be before a shift's start time.");
        }

        UserShiftSpecification userSpec = getSpecification(getSearchCriteriaForString(USERNAME_KEY, ":", "Test"));

        // validate newly created shift does not overlap with an existing shift
        boolean isOverlapping = userShiftDao.getAll(userSpec).stream()
                .anyMatch(userShift -> isShiftOverlappingWithAnother(shiftDto, userShift));
        if(isOverlapping) {
            throw new ShiftIllegalArgumentException("This shift overlaps with an existing shift.");
        }
    }

    private Shift buildResultShift(UserShift userShift) {
        return Shift.builder()
                .id(String.valueOf(userShift.getId()))
                .startTime(formatLocalTime(userShift.getStartTime()))
                .endTime(formatLocalTime(userShift.getEndTime()))
                .build();
    }

    private boolean isShiftOverlappingWithAnother(ShiftDto newUserShift, UserShift existingUserShift) {
        LocalTime newStartTime = convertToTime(newUserShift.getStartHour(), newUserShift.getStartMinute());
        LocalTime newEndTime = convertToTime(newUserShift.getEndHour(), newUserShift.getEndMinute());

        LocalTime existingStartTime = existingUserShift.getStartTime();
        LocalTime existingEndTime = existingUserShift.getEndTime();

        return newStartTime.equals(existingStartTime) ||
                newEndTime.equals(existingEndTime) ||
                isTimeWithinRange(newStartTime, existingStartTime, existingEndTime) ||
                isTimeWithinRange(newEndTime, existingStartTime, existingEndTime) ||
                (newStartTime.isBefore(existingStartTime) && newEndTime.isAfter(existingEndTime));
    }

    private boolean isTimeWithinRange(LocalTime time, LocalTime rangeStartTime, LocalTime rangeEndTime) {
        return time.isAfter(rangeStartTime) && time.isBefore(rangeEndTime);
    }

    private LocalTime convertToTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    private String formatLocalTime(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private UserShiftSpecification getSpecification(SearchCriteria searchCriteria) {
        return new UserShiftSpecification(searchCriteria);
    }

    private SearchCriteria getSearchCriteriaForString(String key, String operation, String value) {
        return new SearchCriteria(key, operation, value);
    }

    private SearchCriteria getSearchCriteriaForTime(String key, String operation, LocalTime time) {
        return new SearchCriteria(key, operation, time);
    }

    private UserShift translateShift(ShiftDto shiftDto) {
        return new UserShift(null, "Test",
                convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()),
                convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));
    }
}
