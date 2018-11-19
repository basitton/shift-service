package shift.service.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shift.domain.dao.UserRepository;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;
import shift.domain.security.UserPrincipal;


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
        user.setPassword(getPasswordEncoder().encode(user.getPassword()));

        return UserPrincipal.create(user);
    }

    private PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SearchCriteria getSearchCriteriaForUsername(String username) {
        return new SearchCriteria("username", ":", username);
    }

    private UserSpecification getUserSpecification(String username) {
        return new UserSpecification(getSearchCriteriaForUsername(username));
    }
}
