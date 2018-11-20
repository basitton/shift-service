package shift.service.User;

import org.h2.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shift.domain.dao.UserRepository;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Provides methods for viewing and managing users
 */
@Service
public class UserService {

    private UserRepository userDao;

    public UserService(UserRepository userDao) {
        this.userDao = userDao;
    }

    // encapsulation
    private SearchCriteria getSearchCriteriaForUsername(String username) {
        return new SearchCriteria("username", ":", username);
    }

    // encapsulation
    private UserSpecification getUserSpecification(String username) {
        return new UserSpecification(getSearchCriteriaForUsername(username));
    }

    /**
     * Gets a {@link User} entity from the database by username
     * @param username username to find user
     * @return {@link User} entity object
     * @throws UsernameNotFoundException when no user exists with the given username
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        UserSpecification userSpecification = getUserSpecification(getCurrentUsername(username));
        return userDao.findOne(userSpecification)
                .orElseThrow(() -> new UsernameNotFoundException("No username exists with username " + username));
    }

    /**
     * Validates the user exists within the database
     * @param username the username to validate
     * @throws UsernameNotFoundException when no user exists with the given username
     */
    public void validateUser(@NotNull String username) throws UsernameNotFoundException {
        getUserByUsername(username);
    }

    /**
     * Gets all users from the database
     * @return a list of {@link User}
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * Gets the username to be used for the current session.
     * If the passed in username is empty, returns the user currently stored in the application's security context (from the token)
     * @param username the username to check
     * @return the current user to be used
     */
    public String getCurrentUsername(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return getCurrentUsername();
        }
        return username;
    }

    /**
     * Creates a user to be stored in order to use the application
     * @param user {@link User}
     */
    public void createUser(User user) {
        userDao.save(user);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
