package shift.service.Shift;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods for viewing and managing shifts in general, and viewing and managing shifts by user.
 */
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

    /**
     * Gets all stored shifts within the given time frame.
     * @param searchShiftDto the {@link SearchShiftDto} for restricting the search
     * @return the results from the search query
     */
    public List<ResultShiftDto> getAllShifts(@NotNull SearchShiftDto searchShiftDto) {
        LocalTime searchStartTime = convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        // specifications allow for table parameter search in H2 DB
        ShiftSpecification startSpec = getShiftSpecificationForTime(START_TIME_KEY, ">:", searchStartTime);
        ShiftSpecification endSpec = getShiftSpecificationForTime(END_TIME_KEY, "<:", searchEndTime);

        return shiftDao.findAll(Specification.where(startSpec).and(endSpec), getSortShiftByStartTime()).stream()
                .map(this::buildResultShift)
                .collect(Collectors.toList());
    }

    /**
     * Gets all stored shifts for a specific user within the given time frame.
     * @param username the user's shifts that are being searched
     * @param searchShiftDto the {@link SearchShiftDto} for restricting the search
     * @return the results from the search query
     */
    public List<ResultShiftDto> searchShifts(String username, @NotNull SearchShiftDto searchShiftDto) {
        String user = userService.getCurrentUsername(username);
        userService.validateUser(user);

        LocalTime searchStartTime = convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        // specifications allow for table parameter search in H2 DB
        ShiftSpecification userSpec = getShiftSpecificationForUsername(user);
        ShiftSpecification startSpec = getShiftSpecificationForTime(START_TIME_KEY, ">:", searchStartTime);
        ShiftSpecification endSpec = getShiftSpecificationForTime(END_TIME_KEY, "<:", searchEndTime);

        return shiftDao.findAll(Specification.where(userSpec).and(startSpec).and(endSpec), getSortShiftByStartTime()).stream()
                .map(this::buildResultShift)
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific user's shift by its id
     * @param username the username of the user for which shifts are being searched
     * @param shiftId the shift's id
     * @return the user's shift for the given id
     * @throws UsernameNotFoundException when there is no user existing for the given username
     * @throws ShiftNotFoundException when there is no shift existing with that id for the specified user
     */
    public ResultShiftDto getShiftForUser(String username, @NotNull long shiftId) throws UsernameNotFoundException, ShiftNotFoundException {
        return buildResultShift(getShiftFromDbForUser(username, shiftId));
    }

    /**
     * Creates the shift for a given user
     * <p>
     * Does the below validations:
     * 1. The username given in {@link ShiftDto} is an existing username
     * 2. The shift's start time is not the same as the shift's end time
     * 3. The shift's end time is not set before the shift's start time
     * 4. The updated shift does not overlap with another shift of the user
     * </p>
     * @param shiftDto the {@link ShiftDto} providing parameters for creating the shift
     * @return the result of the shift creation
     * @throws UsernameNotFoundException when the specified user does not exist
     * @throws ShiftIllegalArgumentException when the given params from the shiftDto violates rules listed in the method description
     */
    public ResultShiftDto createShift(@NotNull ShiftDto shiftDto) throws UsernameNotFoundException, ShiftIllegalArgumentException {
        doShiftValidations(shiftDto, null);

        Shift shift = translateDtoToShift(shiftDto);

        shiftDao.save(shift);
        return buildResultShift(shift);
    }

    /**
     * Updates the shift for the given user
     * <p>
     * Does the below validations:
     * 1. The username given in {@link ShiftDto} is an existing username
     * 2. The shift's start time is not the same as the shift's end time
     * 3. The shift's end time is not set before the shift's start time
     * 4. The updated shift does not overlap with another shift of the user
     * </p>
     * @param shiftId the shift to be updated
     * @param shiftDto the updated parameters {@link ShiftDto}. A username must be specified here (or it will default to the currently logged in user) in order to do rule validations
     * @return the result of the shift update
     * @throws ShiftNotFoundException when there is no shift existing for the given username with the given id
     * @throws ShiftIllegalArgumentException when the given params from the shiftDto violates rules listed in the method description
     * @throws UsernameNotFoundException when the specified user does not exist
     */
    public ResultShiftDto updateShift(@NotNull long shiftId, @NotNull ShiftDto shiftDto) throws ShiftNotFoundException, ShiftIllegalArgumentException, UsernameNotFoundException {
        doShiftValidations(shiftDto, shiftId);

        // only make updates to the time of the existing shift
        Shift existingShift = getShiftFromDbForUser(shiftDto.getUsername(), shiftId);
        existingShift.setStartTime(convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()));
        existingShift.setEndTime(convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));

        shiftDao.save(existingShift);

        return buildResultShift(existingShift);
    }

    /**
     * Deletes the shift for the specified user
     * @param username the user's shift to be deleted
     * @param shiftId the id of the shift to be deleted
     * @throws ShiftNotFoundException when the shift for the given user with the given id does not exist
     * @throws UsernameNotFoundException when the specified user does not exist
     */
    public void deleteUserShift(@NotEmpty String username, @NotNull long shiftId) throws ShiftNotFoundException, UsernameNotFoundException {
        shiftDao.delete(getShiftFromDbForUser(username, shiftId));
    }

    /**
     * Deletes the shift
     * @param shiftId the id of the shift to be deleted
     * @throws ShiftNotFoundException when the shift for the given user with the given id does not exist
     */
    public void deleteShift(@NotNull long shiftId) throws ShiftNotFoundException {
        shiftDao.delete(getShiftFromDb(shiftId));
    }

    private Shift getShiftFromDb(@NotNull long shiftId) throws ShiftNotFoundException {
        return shiftDao.findById(shiftId)
                .orElseThrow(() -> new ShiftNotFoundException("Unable to find shift with id " + shiftId));
    }

    private Shift getShiftFromDbForUser(String username, @NotNull long shiftId) throws ShiftNotFoundException {
        String user = userService.getCurrentUsername(username);
        userService.validateUser(user);

        // specifications allow for table parameter search in H2 DB
        ShiftSpecification shiftUsernameSpec = getShiftSpecificationForUsername(user);
        ShiftSpecification shiftIdSpecification = getShiftSpecificationForShiftId(shiftId);

        return shiftDao.findOne(Specification.where(shiftIdSpecification).and(shiftUsernameSpec))
                .orElseThrow(() -> new ShiftNotFoundException("Unable to find shift for username " + user + " with id " + shiftId));
    }

    private void validateUserShift(ShiftDto shiftDto, Long shiftId) throws ShiftIllegalArgumentException {
        String username = shiftDto.getUsername();
        ShiftSpecification userSpec = getShiftSpecificationForUsername(username);

        // validate newly created shift does not overlap with an existing shift
        // validates against each shift found in the DB for the specified user that is not the shift being updated
        boolean isOverlapping = shiftDao.findAll(userSpec).stream()
                .anyMatch(userShift -> (!isUpdatedShiftSameAsExistingShift(shiftId, userShift) &&
                        isShiftOverlappingWithAnother(shiftDto, userShift)));
        if(isOverlapping) {
            throw new ShiftIllegalArgumentException("This shift overlaps with an existing shift for username " + username);
        }
    }

    private boolean isUpdatedShiftSameAsExistingShift(Long updatedShiftId, Shift existingShift) {
        if (updatedShiftId != null) {
            return updatedShiftId.toString().equals(existingShift.getId().toString());
        }
        return false;
    }

    // encapsulation
    private void doShiftValidations(ShiftDto shiftDto, Long shiftId) throws ShiftIllegalArgumentException {
        userService.validateUser(shiftDto.getUsername());
        validateShiftTimes(shiftDto);
        validateUserShift(shiftDto, shiftId);
    }

    // encapsulation
    private ResultShiftDto buildResultShift(Shift shift) {
        return ResultShiftDto.builder()
                .id(shift.getId())
                .user(shift.getUsername())
                .startTime(formatLocalTime(shift.getStartTime()))
                .endTime(formatLocalTime(shift.getEndTime()))
                .build();
    }

    // encapsulation
    private Sort getSortShiftByStartTime() {
        return new Sort(Sort.DEFAULT_DIRECTION, "startTime");
    }

    // encapsulation
    private ShiftSpecification getShiftSpecificationForUsername(String username) {
        return new ShiftSpecification(getSearchCriteriaForShiftUsername(userService.getCurrentUsername(username)));
    }

    // encapsulation
    private SearchCriteria getSearchCriteriaForShiftUsername(String username)  {
        return new SearchCriteria("username", ":", username);
    }

    // encapsulation
    private ShiftSpecification getShiftSpecificationForShiftId(long id) {
        return new ShiftSpecification(getSearchCriteriaForShiftId(id));
    }

    // encapsulation
    private SearchCriteria getSearchCriteriaForShiftId(long id) {
        return new SearchCriteria("id", ":", id);
    }

    // encapsulation
    private ShiftSpecification getShiftSpecificationForTime(String key, String operation, LocalTime time) {
        return  new ShiftSpecification(getSearchCriteriaForShiftTime(key, operation, time));
    }

    // encapsulation
    private SearchCriteria getSearchCriteriaForShiftTime(String key, String operation, LocalTime time) {
        return new SearchCriteria(key, operation, time);
    }

    // encapsulation
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

    // checks if the new shift overlaps the existing shift, but allows for the new shift's endpoints to overlap with the existing shift's endpoints
    // (i.e. existing shift can end at 1:30pm and new shift can start at 1:30pm)
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
