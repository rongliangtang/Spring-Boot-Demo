package cn.tangrl.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.tangrl.server.model.User;
import cn.tangrl.server.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers  // 启用 Testcontainers 支持，用于在测试中使用容器化的数据库
@SpringBootTest  // 运行 Spring Boot 上下文，用于集成测试
public class UserServiceIT {

    @Container  // 定义一个 MySQL 容器，用于在测试过程中模拟真实的 MySQL 数据库
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
        "mysql:8.0.33").withDatabaseName("testdb").withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private UserService userService;  // 注入 UserService 用于测试
    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 用于操作数据库

    @DynamicPropertySource  // 动态配置测试数据库属性，将容器中的数据库连接信息注入 Spring 的环境配置中
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
    void setUp() {
        // 在每个测试之前准备数据
    }

    @AfterEach
    void tearDown() {
        // 清理数据库中的数据，确保测试环境的独立性
        userRepository.deleteAll();
    }

    @Test
    void testFindUsersByName() {
        // Arrange - 设置测试数据
        User user1 = new User(null, "John Doe", "john.doe@example.com");
        User user2 = new User(null, "Jane Doe", "jane.doe@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Act - 调用 UserService 方法进行查询
        List<User> foundUsers = userService.findUsersByName("John Doe");

        // Assert - 验证查询结果是否正确
        assertEquals(1, foundUsers.size());
        assertEquals("John Doe", foundUsers.get(0).getName());
    }
}
