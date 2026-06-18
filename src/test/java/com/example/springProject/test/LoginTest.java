package com.example.springProject.test;

import com.example.springproject.model.Users;
import com.example.springproject.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    public UserDetailsServiceImpl userDetailsService;

    public static Stream<Users> loginResource(){
        Users u1 = new Users();
        u1.setUserName("tina1");
        u1.setPassword("1111");
        u1.setRole("user");

        Users u2 = new Users();
        u2.setUserName("tina2");
        u2.setPassword("2222");
        u2.setRole("user");

        Users u3 = new Users();
        u3.setUserName("tina3");
        u3.setPassword("3333");
        u3.setRole("user");
        return Stream.of(u1,u2,u3);
    }
    @ParameterizedTest
    @MethodSource("loginResource")
    public void logintestSuccess(Users users){
        doReturn(users).when(jdbcTemplate).queryForObject(any(),any(), any(RowMapper.class));
        UserDetails userDetails = userDetailsService.loadUserByUsername(users.getUserName());
        assertNotNull(userDetails);
    }

  @Test
    public void logintestFailed(){
        doThrow(new RuntimeException("no found")).when(jdbcTemplate).queryForObject(any(),any(), any(RowMapper.class));
        assertThrows(UsernameNotFoundException.class, ()->userDetailsService.loadUserByUsername("username"));
  }
}
