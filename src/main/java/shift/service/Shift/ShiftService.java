package shift.service.Shift;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shift.domain.dao.ShiftRepository;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.Shift.Shift;
import shift.domain.h2.Shift.ShiftSpecification;
import shift.exception.ShiftIllegalArgumentException;
import shift.service.User.UserService;

import java.time.LocalTime;
import java.util.List;

@Service
public class ShiftService {

    private ShiftRepository shiftDao;
    private UserService userService;

    public ShiftService(ShiftRepository shiftDao, UserService userService) {
        this.shiftDao = shiftDao;
        this.userService = userService;
    }

    public List<Shift> getAllShifts() {
        return shiftDao.findAll(getSortShiftByStartTime());
    }

    public Sort getSortShiftByStartTime() {
        return new Sort(Sort.DEFAULT_DIRECTION, "startTime");
    }

    public ShiftSpecification getShiftSpecificationForUsername(String username) {
        return new ShiftSpecification(getSearchCriteriaForShiftUsername(username));
    }

    private SearchCriteria getSearchCriteriaForShiftUsername(String username)  {
        return new SearchCriteria("username", ":", username);
    }

    public ShiftSpecification getShiftSpecificationForShiftId(long id) {
        return new ShiftSpecification(getSearchCriteriaForShiftId(id));
    }

    private SearchCriteria getSearchCriteriaForShiftId(long id) {
        return new SearchCriteria("id", ":", id);
    }

    public ShiftSpecification getShiftSpecificationForTime(String key, String operation, LocalTime time) {
        return  new ShiftSpecification(getSearchCriteriaForShiftTime(key, operation, time));
    }

    private SearchCriteria getSearchCriteriaForShiftTime(String key, String operation, LocalTime time) {
        return new SearchCriteria(key, operation, time);
    }

    public Shift translateDtoToShift(ShiftDto shiftDto) {
        return new Shift(null, userService.getCurrentUsername(shiftDto.getUsername()),
                convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()),
                convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));
    }

    public void validateShiftTimes(ShiftDto shiftDto) {
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
    }

    public boolean isShiftOverlappingWithAnother(ShiftDto newUserShift, Shift existingShift) {
        LocalTime newStartTime = convertToTime(newUserShift.getStartHour(), newUserShift.getStartMinute());
        LocalTime newEndTime = convertToTime(newUserShift.getEndHour(), newUserShift.getEndMinute());

        LocalTime existingStartTime = existingShift.getStartTime();
        LocalTime existingEndTime = existingShift.getEndTime();

        return newStartTime.equals(existingStartTime) ||
                newEndTime.equals(existingEndTime) ||
                isTimeWithinRange(newStartTime, existingStartTime, existingEndTime) ||
                isTimeWithinRange(newEndTime, existingStartTime, existingEndTime) ||
                (newStartTime.isBefore(existingStartTime) && newEndTime.isAfter(existingEndTime));
    }

    private boolean isTimeWithinRange(LocalTime time, LocalTime rangeStartTime, LocalTime rangeEndTime) {
        return time.isAfter(rangeStartTime) && time.isBefore(rangeEndTime);
    }

    public LocalTime convertToTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }
}
