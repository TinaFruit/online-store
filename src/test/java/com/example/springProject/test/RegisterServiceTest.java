package com.example.springProject.test;

import com.example.springproject.exeption.DuplicateRegisterException;
import com.example.springproject.model.Users;
import com.example.springproject.repository.RegisterRepo;
import com.example.springproject.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class) //---没有它，@Mock 和 @InjectMocks 不会生效：
public class RegisterServiceTest {
//@Autowired  →  连真实数据库
//@Mock       →  造假数据库
    @Mock
    private RegisterRepo registerRepo;      // 造假

    @Mock
    private PasswordEncoder passwordEncoder; // 造假 如果不 Mock PasswordEncoder，它就是 null，调用 passwordEncoder.encode() 就会报 NullPointerException，测试会崩掉。
//    所以凡是 Service 里用到的东西，都要 Mock。

    @InjectMocks
    private RegisterService registerService; // 自动把上面两个假的注入进去
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // ← 每个测试前重置
    }

    @Test
    void testDuplicatedUserName() {
        Users user = new Users();
        user.setUserName("tina");
        user.setPassword("123456");

        // 告诉假数据库：这个用户存在
//        when(registerRepo.checkDuplicatedRegister(user)).thenReturn(true);
        doReturn(true).when(registerRepo).checkDuplicatedRegister(user);
        // 验证抛出异常
        assertThrows(DuplicateRegisterException.class, () -> {
            registerService.registerServ(user);
        });
    }
}
//    @Test
//    public void testDuplicatedUserName(String duplicatedUsername){
//        Integer result = registerMapper.checkDuplicatedRegister(duplicatedUsername); //设定数据库中有这个duplicatedUsername
//        assertEquals(result,1);
//
//    }
//    @Test
//    public void testNoDuplicatedUserName(String newusername){
//        Integer result = registerMapper.checkDuplicatedRegister(newusername); ////设定数据库中没有这个duplicatedUsername
//        assertEquals(result,0);
//    }
