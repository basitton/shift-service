package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shift.domain.dto.ResultShiftDto;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.Shift.Shift;
import shift.exception.ShiftNotFoundException;
import shift.service.Shift.ShiftService;

import java.util.List;

@RestController
public class ShiftController {

    private ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/shifts/all")
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/shifts")
    public ResultShiftDto createShift(@RequestBody ShiftDto shiftDto) {
        return shiftService.createShift(shiftDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/shifts/{shiftId}")
    public ResultShiftDto getShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getShift(null, shiftId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/shifts/users/{username}/{shiftId}")
    public ResultShiftDto getShiftForUser(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getShift(username, shiftId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/shifts/{shiftId}")
    public ResultShiftDto updateShift(@PathVariable(value = "shiftId") long shiftId, @RequestBody ShiftDto shiftDto) throws ShiftNotFoundException {
        return shiftService.updateShift(shiftId, shiftDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/shifts/users/{username}/{shiftId}")
    public void deleteShift(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        shiftService.deleteShift(username, shiftId);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/shifts")
    public List<ResultShiftDto> getAllCurrentUserShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                               @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                               @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                               @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(null, new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/shifts/users/{username}")
    public List<ResultShiftDto> getAllShiftsForUser(@PathVariable(value = "username") String username,
                                                    @RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(username, new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }
}
