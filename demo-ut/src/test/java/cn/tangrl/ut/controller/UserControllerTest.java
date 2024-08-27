package cn.tangrl.ut.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.tangrl.ut.model.User;
import cn.tangrl.ut.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)  // 仅加载与 UserController 相关的 Web 层组件进行测试
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 注入 MockMvc 用于模拟 HTTP 请求

    @MockBean
    private UserService userService;  // 模拟 UserService 依赖

    @Test
    void testGetUsersByName() throws Exception {
        // Arrange - 设置测试数据和模拟行为
        User user = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.findUsersByName("John Doe")).thenReturn(List.of(user));

        // Act & Assert - 模拟 GET 请求并验证响应
        mockMvc.perform(get("/api/users/name/John Doe"))
            .andExpect(status().isOk())  // 期望状态码为 200 OK
            .andExpect(jsonPath("$[0].name").value("John Doe"))  // 验证 JSON 响应中的 name 字段
            .andExpect(
                jsonPath("$[0].email").value("john.doe@example.com"));  // 验证 JSON 响应中的 email 字段
    }
}
