package shift.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import shift.domain.h2.Shift.Shift;

/**
 * Data access layer for shift data extends {@link JpaRepository} for interacting with the Java Persistence Api
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {

}
