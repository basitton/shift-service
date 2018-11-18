package shift.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import shift.domain.h2.UserShift;

@Repository
public interface UserShiftRepository extends JpaRepository<UserShift, Long>, JpaSpecificationExecutor<UserShift> {

}
