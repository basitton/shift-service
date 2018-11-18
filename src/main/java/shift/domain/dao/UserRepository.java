package shift.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shift.domain.h2.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { }
