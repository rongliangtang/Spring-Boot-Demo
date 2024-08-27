package cn.tangrl.server.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void testValidEmail() {
        // 测试有效的邮箱地址
        User user = new User(null, "John Doe", "john.doe@example.com");
        assertTrue(user.isValidEmail(), "Email should be valid");
    }

    @Test
    void testInvalidEmail() {
        // 测试无效的邮箱地址
        User user = new User(null, "John Doe", "john.doeexample.com");
        assertFalse(user.isValidEmail(), "Email should be invalid");
    }

    @Test
    void testBlankName() {
        // 测试空白名称的情况
        User user = new User(null, "", "john.doe@example.com");
        assertTrue(user.getName().isEmpty(), "Name should be empty");
    }

    @Test
    void testNotBlankName() {
        // 测试非空名称的情况
        User user = new User(null, "John Doe", "john.doe@example.com");
        assertFalse(user.getName().isEmpty(), "Name should not be empty");
    }
}
