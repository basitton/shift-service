package shift.repository;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import shift.domain.dao.UserShiftRepository;
import shift.domain.h2.UserShift;
import shift.domain.h2.UserShiftSpecification;
import shift.exception.ShiftNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * persistence layer
 */
@Getter
@Setter
@Component
public class UserShiftDao {

    private UserShiftRepository userShiftRepository;

    @Autowired
    public UserShiftDao(UserShiftRepository userShiftRepository) {
        this.userShiftRepository = userShiftRepository;
    }

    public List<UserShift> getAll() {
        return userShiftRepository.findAll(getSortByStartTime());
    }

    public List<UserShift> getAll(UserShiftSpecification userSpec) {
        return userShiftRepository.findAll(Specification.where(userSpec), getSortByStartTime());
    }

    public List<UserShift> getAll(UserShiftSpecification userSpec, UserShiftSpecification startSpec, UserShiftSpecification endSpec) {
        return userShiftRepository.findAll(Specification.where(userSpec).and(startSpec).and(endSpec), getSortByStartTime());
    }

    public UserShift get(long id) throws ShiftNotFoundException {
        try {
            return userShiftRepository.getOne(id);
        } catch (EntityNotFoundException ex) {
            throw new ShiftNotFoundException("Unable to find a shift by id: " + id);
        }
    }

    public void save(UserShift shift) {
        userShiftRepository.save(shift);
    }

    public void update(long id, UserShift updatedUserShift) throws ShiftNotFoundException {
        UserShift shift = get(id);
        updatedUserShift.setId(shift.getId());
        save(updatedUserShift);
    }

    public void delete(long id) throws ShiftNotFoundException {
        userShiftRepository.delete(get(id));
    }

    private Sort getSortByStartTime() {
        return new Sort(Sort.DEFAULT_DIRECTION, "startTime");
    }
}
