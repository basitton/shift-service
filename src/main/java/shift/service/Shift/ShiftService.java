package shift.service.Shift;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import shift.domain.dao.ShiftRepository;
import shift.domain.dto.ResultShiftDto;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.Shift.Shift;
import shift.domain.h2.Shift.ShiftSpecification;
import shift.exception.ShiftIllegalArgumentException;
import shift.exception.ShiftNotFoundException;
import shift.service.User.UserService;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftService {
    private static final String START_TIME_KEY = "startTime";
    private static final String END_TIME_KEY = "endTime";

    private ShiftRepository shiftDao;
    private UserService userService;

    public ShiftService(ShiftRepository userDao, UserService userService) {
        this.shiftDao = userDao;
        this.userService = userService;
    }

    public List<ResultShiftDto> searchShifts(String username, @NotNull SearchShiftDto searchShiftDto) {
        LocalTime searchStartTime = convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        ShiftSpecification userSpec = getShiftSpecificationForUsername(username);
        ShiftSpecification startSpec = getShiftSpecificationForTime(START_TIME_KEY, ">:", searchStartTime);
        ShiftSpecification endSpec = getShiftSpecificationForTime(END_TIME_KEY, "<:", searchEndTime);

        return shiftDao.findAll(Specification.where(userSpec).and(startSpec).and(endSpec), getSortShiftByStartTime()).stream()
                .map(this::buildResultShift)
                .collect(Collectors.toList());
    }

    public Shift getShiftFromDb(String username, @NotNull long shiftId) {
        ShiftSpecification shiftUserIdSpec = getShiftSpecificationForUsername(username);
        ShiftSpecification shiftIdSpecification = getShiftSpecificationForShiftId(shiftId);

        return shiftDao.findOne(Specification.where(shiftUserIdSpec).and(shiftIdSpecification))
                .orElseThrow(() -> new ShiftNotFoundException("Unable to find shift for username " + username + " with id " + shiftId));
    }

    public ResultShiftDto getShift(String username, @NotNull long shiftId) throws SecurityException, ShiftNotFoundException {
        return buildResultShift(getShiftFromDb(username, shiftId));
    }

    public ResultShiftDto updateShift(@NotNull long shiftId, @NotNull ShiftDto shiftDto) throws ShiftNotFoundException, ShiftIllegalArgumentException {
        doShiftValidations(shiftDto);

        Shift existingShift = getShiftFromDb(shiftDto.getUsername(), shiftId);
        existingShift.setStartTime(convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()));
        existingShift.setEndTime(convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));

        shiftDao.save(existingShift);
        // return updated shift result
        return buildResultShift(existingShift);
    }

    public void deleteShift(String username, @NotNull long shiftId) throws ShiftNotFoundException, ShiftIllegalArgumentException {
        shiftDao.delete(getShiftFromDb(username, shiftId));
    }

    public ResultShiftDto createShift(@NotNull ShiftDto shiftDto) {
        doShiftValidations(shiftDto);

        Shift shift = translateDtoToShift(shiftDto);
        // persist shift into "database"
        shiftDao.save(shift);
        return buildResultShift(shift);
    }

    private void validateUserShift(ShiftDto shiftDto) throws ShiftIllegalArgumentException {
        String username = shiftDto.getUsername();
        ShiftSpecification userSpec = getShiftSpecificationForUsername(username);

        // validate newly created shift does not overlap with an existing shift
        boolean isOverlapping = shiftDao.findAll(userSpec).stream()
                .anyMatch(userShift -> isShiftOverlappingWithAnother(shiftDto, userShift));
        if(isOverlapping) {
            throw new ShiftIllegalArgumentException("This shift overlaps with an existing shift for username ." + username);
        }
    }

    private void doShiftValidations(ShiftDto shiftDto) throws SecurityException, ShiftIllegalArgumentException {
        validateUserShift(shiftDto);
        validateShiftTimes(shiftDto);
    }

    private ResultShiftDto buildResultShift(Shift shift) {
        return ResultShiftDto.builder()
                .id(shift.getId())
                .user(shift.getUsername())
                .startTime(formatLocalTime(shift.getStartTime()))
                .endTime(formatLocalTime(shift.getEndTime()))
                .build();
    }

    public List<Shift> getAllShifts() {
        return shiftDao.findAll(getSortShiftByStartTime());
    }

    private Sort getSortShiftByStartTime() {
        return new Sort(Sort.DEFAULT_DIRECTION, "startTime");
    }

    private ShiftSpecification getShiftSpecificationForUsername(String username) {
        return new ShiftSpecification(getSearchCriteriaForShiftUsername(username));
    }

    private SearchCriteria getSearchCriteriaForShiftUsername(String username)  {
        return new SearchCriteria("username", ":", username);
    }

    private ShiftSpecification getShiftSpecificationForShiftId(long id) {
        return new ShiftSpecification(getSearchCriteriaForShiftId(id));
    }

    private SearchCriteria getSearchCriteriaForShiftId(long id) {
        return new SearchCriteria("id", ":", id);
    }

    private ShiftSpecification getShiftSpecificationForTime(String key, String operation, LocalTime time) {
        return  new ShiftSpecification(getSearchCriteriaForShiftTime(key, operation, time));
    }

    private SearchCriteria getSearchCriteriaForShiftTime(String key, String operation, LocalTime time) {
        return new SearchCriteria(key, operation, time);
    }

    private Shift translateDtoToShift(ShiftDto shiftDto) {
        return new Shift(null, userService.getCurrentUsername(shiftDto.getUsername()),
                convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()),
                convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));
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
    }

    private boolean isShiftOverlappingWithAnother(ShiftDto newUserShift, Shift existingShift) {
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

    private LocalTime convertToTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    private String formatLocalTime(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

}
