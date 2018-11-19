package shift.service.User;

import org.h2.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shift.domain.dao.UserRepository;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;

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

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        UserSpecification userSpecification = getUserSpecification(getCurrentUsername(username));
        return userDao.findOne(userSpecification)
                .orElseThrow(() -> new UsernameNotFoundException("No username exists with username " + username));
    }

    public void validateUser(String username) throws UsernameNotFoundException {
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

    public void createUser(User user) {
        userDao.save(user);
    }
}
