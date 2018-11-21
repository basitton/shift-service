package shift.service;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import shift.domain.dao.UserRepository;
import shift.domain.h2.User.User;
import shift.domain.h2.User.UserSpecification;
import shift.service.User.UserService;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    UserRepository userDao;

    @Mock
    SecurityContextHolder securityContextHolder;

    @InjectMocks
    UserService userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserByUsername() {
        String username = "username";
        Optional<User> user = Optional.of(new User(username, "password", Sets.newHashSet()));
        when(userDao.findOne(any(UserSpecification.class))).thenReturn(user);

        assertEquals(user.get(), userService.getUserByUsername(username));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testGetUserByUsernameUserDoeNotExist() {
        when(userDao.findOne(any(UserSpecification.class))).thenReturn(Optional.empty());
        userService.getUserByUsername("username");
    }

    @Test
    public void testValidateUser() {
        String username = "username";
        Optional<User> user = Optional.of(new User(username, "password", Sets.newHashSet()));
        when(userDao.findOne(any(UserSpecification.class))).thenReturn(user);
        userService.validateUser(username);
        verify(userDao, times(1)).findOne(any(UserSpecification.class));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testValidateUserUserDoeNotExist() {
        String username = "username";
        when(userDao.findOne(any(UserSpecification.class))).thenReturn(Optional.empty());
        userService.getUserByUsername("username");
        userService.validateUser(username);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User("username", "password", Sets.newHashSet());
        User user2 = new User("username", "password", Sets.newHashSet());
        List<User> returnedUsers = Lists.newArrayList(user1, user2);

        when(userDao.findAll()).thenReturn(returnedUsers);

        assertEquals(returnedUsers, userService.getAllUsers());
    }

    @Test
    public void testGetCurrentUsernameNotEmpty() {
        String username = "username";
        assertEquals(username, userService.getCurrentUsername(username));
    }

    @Test
    public void testGetCurrentUsernameEmpty() {
        String username = "username";
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn(username);
        assertEquals(username, userService.getCurrentUsername(""));
    }

    @Test
    public void testCreateUser() {
        User user = new User("username", "password", Sets.newHashSet());
        userService.createUser(user);
        verify(userDao, times(1)).save(user);
    }
}
