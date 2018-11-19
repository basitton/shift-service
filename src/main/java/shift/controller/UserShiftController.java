package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.Shift.Shift;
import shift.exception.ShiftNotFoundException;
import shift.service.UserShiftService;

import java.util.List;

@RestController
public class UserShiftController {

    private UserShiftService userShiftService;

    @Autowired
    public UserShiftController(UserShiftService userShiftService) {
        this.userShiftService = userShiftService;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/shifts")
    public Shift createShift(@RequestBody ShiftDto shiftDto) {
        return userShiftService.createShift(shiftDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/shifts/{shiftId}")
    public Shift getShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return userShiftService.getShift(null, shiftId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/shifts/users/{username}/{shiftId}")
    public Shift getShiftForUser(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return userShiftService.getShift(username, shiftId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/shifts/{shiftId}")
    public Shift updateShift(@PathVariable(value = "shiftId") long shiftId, @RequestBody ShiftDto shiftDto) throws ShiftNotFoundException {
        return userShiftService.updateShift(shiftId, shiftDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/shifts/users/{username}/{shiftId}")
    public void deleteShift(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        userShiftService.deleteShift(username, shiftId);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/shifts")
    public List<Shift> getAllCurrentUserShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                               @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                               @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                               @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return userShiftService.searchShifts(null, new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/shifts/users/{username}")
    public List<Shift> getAllShiftsForUser(@PathVariable(value = "username") String username,
                                           @RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                           @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                           @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                           @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return userShiftService.searchShifts(username, new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }
}
