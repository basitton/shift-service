package shift.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import shift.domain.dao.ShiftRepository;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.Shift.Shift;
import shift.domain.h2.Shift.ShiftSpecification;
import shift.exception.ShiftIllegalArgumentException;
import shift.exception.ShiftNotFoundException;
import shift.service.Shift.ShiftService;
import shift.service.User.UserService;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Service
public class UserShiftService {
    private static final String START_TIME_KEY = "startTime";
    private static final String END_TIME_KEY = "endTime";

    private ShiftService shiftService;
    private ShiftRepository shiftDao;

    public UserShiftService(ShiftService shiftService, ShiftRepository userDao) {
        this.shiftService = shiftService;
        this.shiftDao = userDao;
    }

    public List<Shift> searchShifts(String username, @NotNull SearchShiftDto searchShiftDto) {
        LocalTime searchStartTime = shiftService.convertToTime(searchShiftDto.getFromStartHour(), searchShiftDto.getFromStartMinute());
        LocalTime searchEndTime = shiftService.convertToTime(searchShiftDto.getToEndHour(), searchShiftDto.getToEndMinute());

        ShiftSpecification userSpec = shiftService.getShiftSpecificationForUsername(username);
        ShiftSpecification startSpec = shiftService.getShiftSpecificationForTime(START_TIME_KEY, ">:", searchStartTime);
        ShiftSpecification endSpec = shiftService.getShiftSpecificationForTime(END_TIME_KEY, "<:", searchEndTime);

        return shiftDao.findAll(Specification.where(userSpec).and(startSpec).and(endSpec), shiftService.getSortShiftByStartTime());
    }

    public Shift getShift(String username, @NotNull long shiftId) throws SecurityException, ShiftNotFoundException {
        ShiftSpecification shiftUserIdSpec = shiftService.getShiftSpecificationForUsername(username);
        ShiftSpecification shiftIdSpecification = shiftService.getShiftSpecificationForShiftId(shiftId);

        return shiftDao.findOne(Specification.where(shiftUserIdSpec).and(shiftIdSpecification))
                .orElseThrow(() -> new ShiftNotFoundException("Unable to find shift for username " + username + " with id " + shiftId));
    }

    public Shift updateShift(@NotNull long shiftId, @NotNull ShiftDto shiftDto) throws ShiftNotFoundException, ShiftIllegalArgumentException {
        doShiftValidations(shiftDto);

        Shift existingShift = getShift(shiftDto.getUsername(), shiftId);
        existingShift.setStartTime(shiftService.convertToTime(shiftDto.getStartHour(), shiftDto.getStartMinute()));
        existingShift.setEndTime(shiftService.convertToTime(shiftDto.getEndHour(), shiftDto.getEndMinute()));

        shiftDao.save(existingShift);
        // return updated shift result
        return existingShift;
    }

    public void deleteShift(String username, @NotNull long shiftId) throws ShiftNotFoundException, ShiftIllegalArgumentException {
        shiftDao.delete(getShift(username, shiftId));
    }

    public Shift createShift(@NotNull ShiftDto shiftDto) {
        doShiftValidations(shiftDto);

        Shift shift = shiftService.translateDtoToShift(shiftDto);
        // persist shift into "database"
        shiftDao.save(shift);
        return shift;
    }

    private void validateUserShift(ShiftDto shiftDto) throws ShiftIllegalArgumentException {
        String username = shiftDto.getUsername();
        ShiftSpecification userSpec = shiftService.getShiftSpecificationForUsername(username);

        // validate newly created shift does not overlap with an existing shift
        boolean isOverlapping = shiftDao.findAll(userSpec).stream()
                .anyMatch(userShift -> shiftService.isShiftOverlappingWithAnother(shiftDto, userShift));
        if(isOverlapping) {
            throw new ShiftIllegalArgumentException("This shift overlaps with an existing shift for username ." + username);
        }
    }

    private void doShiftValidations(ShiftDto shiftDto) throws SecurityException, ShiftIllegalArgumentException {
        validateUserShift(shiftDto);
        shiftService.validateShiftTimes(shiftDto);
    }

}
