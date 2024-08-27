package cn.tangrl.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.tangrl.server.model.User;
import cn.tangrl.server.repository.UserRepository;
import cn.tangrl.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers  // 启用 Testcontainers 支持，用于在测试中使用容器化的数据库
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 启动带有随机端口的 Spring Boot 应用，用于集成测试
public class UserControllerIT {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
        "mysql:8.0.33").withDatabaseName("testdb").withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private TestRestTemplate restTemplate;  // 使用 TestRestTemplate 进行 REST 调用

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository，用于操作数据库

    @Autowired
    private UserService userService;  // 注入 UserService

    @DynamicPropertySource  // 动态配置测试数据库属性，将容器中的数据库连接信息注入 Spring 的环境配置中
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
        // 每个测试方法执行前的初始化操作
    void setUp() {
        userRepository.deleteAll();  // 清理数据库中的数据，确保测试环境的独立性
        userRepository.save(new User(null, "John Doe", "john.doe@example.com"));  // 添加测试数据
        userRepository.save(new User(null, "Jane Doe", "jane.doe@example.com"));
    }

    @Test
    void testGetUsersByName() {
        // 使用 TestRestTemplate 调用 API 并获取响应
        ResponseEntity<User[]> response = restTemplate.getForEntity("/api/users/name/John Doe",
            User[].class);

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应内容
        User[] users = response.getBody();
        assertNotNull(users);
        assertEquals(1, users.length);
        assertEquals("John Doe", users[0].getName());
        assertEquals("john.doe@example.com", users[0].getEmail());
    }

    @Test
    void testGetUsersByName_NotFound() {
        // 使用 TestRestTemplate 调用 API 并获取响应
        ResponseEntity<User[]> response = restTemplate.getForEntity(
            "/api/users/name/Nonexistent User", User[].class);

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应内容为空
        User[] users = response.getBody();
        assertNotNull(users);
        assertEquals(0, users.length);
    }
}
