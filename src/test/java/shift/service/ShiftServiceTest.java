package shift.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import shift.domain.dao.ShiftRepository;
import shift.domain.dto.ResultShiftDto;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.Shift.Shift;
import shift.service.Shift.ShiftService;
import shift.service.User.UserService;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShiftServiceTest {
    @Mock
    ShiftRepository shiftDao;

    @InjectMocks
    ShiftService shiftService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateShiftWithUsername() throws Exception {
        ShiftDto shiftDto = getDefaultShiftDto();
        ResultShiftDto resultShiftDto = getDefaultResultShiftDto();

        assertEquals(resultShiftDto, shiftService.createShift(shiftDto));
    }

    @Test
    public void testGetShiftById() throws Exception {
        long id = 1L;
        Optional<Shift> existingShift = getDefaultExistingShift();

        Mockito.when(shiftDao.findById(id)).thenReturn(existingShift);

        assertEquals(getDefaultResultShiftDto(), shiftService.getShift(id));
    }

    @Test
    public void testUpdateShift() throws Exception {
        long id = 1L;
        String username = "username";

        Optional<Shift> existingShift = getDefaultExistingShift();

        Mockito.when(shiftDao.findById(id)).thenReturn(existingShift);

        ShiftDto shiftDto = getDefaultShiftDto();
        shiftDto.setUsername(username);

        assertEquals(getDefaultResultShiftDto(), shiftService.updateShift(id, getDefaultShiftDto()));
    }

    @Test
    public void testDeleteShift() throws Exception {
        long id = 1L;
        Optional<Shift> existingShift = getDefaultExistingShift();

        Mockito.when(shiftDao.findById(id)).thenReturn(existingShift);
        shiftService.deleteShift(id);
        verify(shiftDao, times(1)).delete(existingShift.get());
    }

    private ShiftDto getDefaultShiftDto() {
        return ShiftDto.builder()
                .startHour(0)
                .startMinute(0)
                .endHour(8)
                .endMinute(0)
                .username("username")
                .build();
    }

    private ResultShiftDto getDefaultResultShiftDto() {
        return ResultShiftDto.builder()
                .startTime("12:00 AM")
                .endTime("08:00 AM")
                .build();
    }

    private Optional<Shift> getDefaultExistingShift() {
        return Optional.of(Shift.builder()
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.of(8, 0))
                .build());
    }
}
