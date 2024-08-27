package cn.tangrl.server.controller;

import cn.tangrl.server.model.User;
import cn.tangrl.server.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据姓名查找用户
    @GetMapping("/name/{name}")
    public List<User> getUsersByName(@PathVariable String name) {
        return userService.findUsersByName(name);
    }
}
