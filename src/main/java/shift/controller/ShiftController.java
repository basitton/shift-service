package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shift.domain.dto.ResultShiftDto;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.exception.ShiftNotFoundException;
import shift.service.Shift.ShiftService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/all")
    public List<ResultShiftDto> getAllShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {
        return shiftService.getAllShifts(getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("")
    public ResultShiftDto createShift(@Valid @RequestBody ShiftDto shiftDto) {
        return shiftService.createShift(shiftDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/{shiftId}")
    public ResultShiftDto getShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getAnyShiftFromDb(shiftId);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("/users/{username}/{shiftId}")
    public ResultShiftDto getShiftForUser(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getShift(username, shiftId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{shiftId}")
    public ResultShiftDto updateShift(@PathVariable(value = "shiftId") long shiftId, @Valid @RequestBody ShiftDto shiftDto) throws ShiftNotFoundException {
        return shiftService.updateShift(shiftId, shiftDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/users/{username}/{shiftId}")
    public void deleteShift(@PathVariable(value = "username") String username, @PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        shiftService.deleteShift(username, shiftId);
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER, ROLE_EMPLOYEE')")
    @GetMapping("")
    public List<ResultShiftDto> getAllCurrentUserShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                               @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                               @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                               @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(null, getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/users/{username}")
    public List<ResultShiftDto> getAllShiftsForUser(@PathVariable(value = "username") String username,
                                                    @RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(username, getSearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }

    private SearchShiftDto getSearchShiftDto(int fromStartHour, int fromStartMinute, int toEndHour, int toEndMinute) {
        return new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute);
    }
}
