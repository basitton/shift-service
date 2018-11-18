package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shift.domain.Shift;
import shift.domain.dto.SearchShiftDto;
import shift.domain.dto.ShiftDto;
import shift.exception.ShiftNotFoundException;
import shift.service.ShiftService;

import javax.validation.Valid;

import java.util.List;


@RestController
public class ShiftController {
    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @RequestMapping("/")
    public String index() {
        return "Welcome to the shift service. " +
                "Please check the README to find out all the cool features this API can do!";
    }

    @PostMapping("/shifts")
    public Shift createShift(@Valid @RequestBody ShiftDto shiftDto) {
        return shiftService.createShift(shiftDto);
    }

    @GetMapping("/shifts/{shiftId}")
    public Shift getShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        return shiftService.getShift(shiftId);
    }

    @PutMapping("/shifts/{shiftId}")
    public Shift updateShift(@PathVariable(value = "shiftId") long shiftId, @RequestBody ShiftDto shiftDto) throws ShiftNotFoundException {
        return shiftService.updateShift(shiftId, shiftDto);
    }

    @DeleteMapping("/shifts/{shiftId}")
    public void deleteShift(@PathVariable(value = "shiftId") long shiftId) throws ShiftNotFoundException {
        shiftService.deleteShift(shiftId);
    }

    @GetMapping("/shifts")
    public List<Shift> getAllShifts(@RequestParam(value = "fromStartHour", defaultValue = "0", required = false) int fromStartHour,
                                    @RequestParam(value = "fromStartMinute", defaultValue = "0", required = false) int fromStartMinute,
                                    @RequestParam(value = "toEndHour", defaultValue = "23", required = false) int toEndHour,
                                    @RequestParam(value = "toEndMinute", defaultValue = "59", required = false) int toEndMinute) {


        return shiftService.searchShifts(new SearchShiftDto(fromStartHour, fromStartMinute, toEndHour, toEndMinute));
    }
}
