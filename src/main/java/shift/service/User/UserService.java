package shift.service.User;

import org.h2.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shift.domain.dao.UserRepository;
import shift.domain.dto.ShiftDto;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.Shift.ShiftSpecification;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;
import shift.exception.ShiftIllegalArgumentException;

import java.util.List;

@Service
public class UserService {

    private UserRepository userDao;

    public UserService(UserRepository userDao) {
        this.userDao = userDao;
    }

    private SearchCriteria getSearchCriteriaForUsername(String username) {
        return new SearchCriteria("username", ":", username);
    }

    private UserSpecification getUserSpecification(String username) {
        return new UserSpecification(getSearchCriteriaForUsername(username));
    }

    public User getUserByUsername(String username) {
        UserSpecification userSpecification = getUserSpecification(getCurrentUsername(username));
        return userDao.findOne(userSpecification)
                .orElseThrow(() -> new SecurityException("No username exists with username " + username));
    }

    void validateUser(String username) {
        getUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public String getCurrentUsername(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return getCurrentUsername();
        }
        return username;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
