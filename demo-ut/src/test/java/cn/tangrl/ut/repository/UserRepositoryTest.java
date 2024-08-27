package cn.tangrl.ut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.tangrl.ut.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataJpaTest  // 使用此注解，进行 JPA 相关的测试，加载内存数据库
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 进行测试

    @DynamicPropertySource  // 动态设置测试环境下的数据库相关属性
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.url",
            () -> "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=true");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.H2Dialect");
    }

    @Test
    void testFindByName() {
        // Arrange - 设置测试数据
        User user1 = new User(null, "John Doe", "john.doe@example.com");
        User user2 = new User(null, "Jane Doe", "jane.doe@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Act - 执行查询操作
        List<User> johns = userRepository.findByName("John Doe");
        List<User> janes = userRepository.findByName("Jane Doe");

        // Assert - 验证查询结果
        assertEquals(1, johns.size(), "Should find one John Doe");
        assertEquals("John Doe", johns.get(0).getName());

        assertEquals(1, janes.size(), "Should find one Jane Doe");
        assertEquals("Jane Doe", janes.get(0).getName());
    }
}
