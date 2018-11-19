package shift.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.Shift.Shift;
import shift.service.Shift.ShiftService;

import javax.validation.Valid;

import java.util.List;


@RestController
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ShiftController {
    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping("/shifts/all")
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }
}
