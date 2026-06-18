package com.example.springProject.test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.example.springproject.model.Users;
import com.example.springproject.repository.RegisterRepo;
import com.example.springproject.service.RegisterService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class Registertest {

    @Mock
    private RegisterRepo registerRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegisterService registerService;

    static Stream<Users> provideUsers(){
        Users user1 = new Users();
        user1.setUserName("tina");
        user1.setPassword("1111");

        Users user2 = new Users();
        user2.setUserName("john");
        user2.setPassword("2222");

        Users user3 = new Users();
        user3.setUserName("mary");
        user3.setPassword("3333");

        return Stream.of(user1, user2, user3);
    }

    @ParameterizedTest
    @MethodSource("provideUsers")
    public void testRegisterNoDuplidate(Users users){
        doReturn(false).when(registerRepo).checkDuplicatedRegister(users);
        doReturn(users.getPassword()).when(passwordEncoder).encode(users.getPassword());
        doReturn(true).when(registerRepo).registerRepo(users);
        assertTrue(registerService.registerServ(users));
    }

}
