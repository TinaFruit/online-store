package com.example.springProject.test;

import com.example.springproject.model.Users;
import com.example.springproject.repository.CommonUtil;
import com.example.springproject.repository.UpdateUserRepo;
import com.example.springproject.service.UpdateUserSer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LoginUpdateInfor {

    @Mock
    private UpdateUserRepo updateUserRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CommonUtil commonUtil;
    @InjectMocks
    private UpdateUserSer updateUserSer;

    static Stream<Users> loginInfor() {
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
        return Stream.of(u1, u2, u3);
    }

    @ParameterizedTest
    @MethodSource("loginInfor")
    public void testLogin(Users user) {

        doReturn(user.getPassword()).when(commonUtil).checkUserAndGetPassword(user.getUserName());
        doReturn(true).when(passwordEncoder).matches(user.getPassword(), user.getPassword());
        doReturn(true).when(updateUserRepo).updateUserNamerep(user);

        assertTrue(updateUserSer.updateUserNameSer(user));
    }


    @ParameterizedTest
    @MethodSource("loginInfor")
    public void testUpdatePasswordOrEmail(Users user) {

        doReturn(user.getPassword()).when(commonUtil).checkUserAndGetPassword(user.getUserName());
        doReturn(true).when(passwordEncoder).matches(any(), any());
        doReturn(true).when(updateUserRepo).updateUserPasswordOrEmailRepo(user);

        assertTrue(updateUserSer.updateUserPasswordOrEmailServ(user));
    }
}
