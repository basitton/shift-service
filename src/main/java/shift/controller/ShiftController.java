package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import shift.domain.dto.ResultShiftDto;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.exception.ShiftIllegalArgumentException;
import shift.exception.ShiftNotFoundException;
import shift.service.Shift.ShiftService;

import javax.validation.Valid;
import java.util.List;

/**
 * Provides endpoints for managing shifts
 */
@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    /**
     * Gets all shifts in the database. Only a manager can access this endpoint.
     * Takes search params that are given in integers for a 24-hour clock.
     * @param fromStartHour the beginning hour of the search request. Defaults to 0 (12am)
     * @param fromStartMinute the beginning minute of the search request. Defaults to 0
     * @param toEndHour the end hour of the search request. Defaults to 23 (11pm)
     * @param toEndMinute the end hour of the search request. Defaults to 59
     * @return A list of {@link ResultShiftDto} shifts
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    public List<ResultShiftDto> getAllShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {
        return shiftService.getAllShifts(getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    /**
     * Creates a shift. Only a manager can access this endpoint
     * @param shiftDto A {@link ShiftDto} with shift parameters and user the shift is being created for
     * @return {@link ResultShiftDto} to confirm how the shift was created.
     * @throws UsernameNotFoundException when the given username does not exist
     * @throws ShiftIllegalArgumentException when the shift overlaps with another existing shift for the specified user
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("")
    public ResultShiftDto createShift(@Valid @RequestBody ShiftDto shiftDto) throws UsernameNotFoundException, ShiftIllegalArgumentException {
        return shiftService.createShift(shiftDto);
    }

    /**
     * Gets a single shift from the database.
     * Only managers and employees can access this endpoint.
     * @param shiftId the unique id of the shift
     * @return A {@link ResultShiftDto}
     * @throws ShiftNotFoundException when no shift with the given id, for the currently logged in user, exists
     */
    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/{shiftId}")
    public ResultShiftDto getShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getShift(shiftId);
    }

    /**
     * Updates a shift. Only a manager can access this endpoint.
     * @param shiftId a unique id of the shift to update
     * @param shiftDto A {@link ShiftDto} with shift parameters and user the shift is being created for. Username is ignored here and generated within methods.
     * @return {@link ResultShiftDto} to confirm how the shift was updated.
     * @throws ShiftIllegalArgumentException when the shift overlaps with another existing shift for the specified user
     * @throws ShiftNotFoundException when no shift with the given id exists
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{shiftId}")
    public ResultShiftDto updateShift(@PathVariable(value = "shiftId") long shiftId, @Valid @RequestBody ShiftDto shiftDto) throws ShiftNotFoundException, ShiftIllegalArgumentException {
        return shiftService.updateShift(shiftId, shiftDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{shiftId}")
    public void deleteShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        shiftService.deleteShift(shiftId);
    }

    /**
     * Search all roles for currently logged in user. Only managers and employees can access this endpoint.
     * Takes search params that are given in integers for a 24-hour clock.
     * @param fromStartHour the beginning hour of the search request. Defaults to 0 (12am)
     * @param fromStartMinute the beginning minute of the search request. Defaults to 0
     * @param toEndHour the end hour of the search request. Defaults to 23 (11pm)
     * @param toEndMinute the end hour of the search request. Defaults to 59
     * @return A list of {@link ResultShiftDto} shifts
     */
    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("")
    public List<ResultShiftDto> getAllCurrentUserShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                               @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                               @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                               @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(null, getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    /**
     * Search all roles for a specific user. Only managers can access this endpoint.
     * Takes search params that are given in integers for a 24-hour clock.
     * @param username the username for which shifts are being searched
     * @param fromStartHour the beginning hour of the search request. Defaults to 0 (12am)
     * @param fromStartMinute the beginning minute of the search request. Defaults to 0
     * @param toEndHour the end hour of the search request. Defaults to 23 (11pm)
     * @param toEndMinute the end hour of the search request. Defaults to 59
     * @return A list of {@link ResultShiftDto} shifts
     * @throws UsernameNotFoundException when the given username does not exist
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/users/{username}")
    public List<ResultShiftDto> getAllShiftsForUser(@PathVariable(value = "username") String username,
                                                    @RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) throws UsernameNotFoundException {


        return shiftService.searchShifts(username, getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    // encapsulation
    private SearchShiftDto getSearchShiftDto(int fromStartHour, int fromStartMinute, int toEndHour, int toEndMinute) {
        return new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute);
    }
}
