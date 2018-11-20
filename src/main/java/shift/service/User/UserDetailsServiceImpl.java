package shift.service.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shift.domain.dao.UserRepository;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;
import shift.domain.security.UserPrincipal;

/**
 * Implements {@link UserDetailsService} for authenticating and transporting {@link UserDetails} within the authentication manager
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userDao;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, SecurityException {

        UserSpecification userSpecification = getUserSpecification(username);

        User user = userDao.findOne(userSpecification)
                .orElseThrow(() -> new SecurityException("Username does not exist."));
        user.setPassword(user.getPassword());

        return UserPrincipal.create(user);
    }

    // encapsulation
    private SearchCriteria getSearchCriteriaForUsername(String username) {
        return new SearchCriteria("username", ":", username);
    }

    // encapsulation
    private UserSpecification getUserSpecification(String username) {
        return new UserSpecification(getSearchCriteriaForUsername(username));
    }
}
