package cn.tangrl.ut.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cn.tangrl.ut.model.User;
import cn.tangrl.ut.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;  // 注入 UserService 实例用于测试

    @Mock
    private UserRepository userRepository;  // 模拟 UserRepository 依赖

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // 初始化 Mockito 注解
    }

    @Test
    void testFindUsersByName() {
        // Arrange - 设置测试数据和模拟行为
        User user = new User(1L, "John Doe", "john.doe@example.com");
        when(userRepository.findByName("John Doe")).thenReturn(List.of(user));

        // Act - 调用被测试的方法
        List<User> users = userService.findUsersByName("John Doe");

        // Assert - 验证结果和方法调用次数
        assertEquals(1, users.size(), "Should return one user");
        assertEquals("John Doe", users.get(0).getName());
        verify(userRepository, times(1)).findByName("John Doe");
    }
}
