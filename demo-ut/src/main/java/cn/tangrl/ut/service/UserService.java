package cn.tangrl.ut.service;

import cn.tangrl.ut.model.User;
import cn.tangrl.ut.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 根据姓名查找用户
    public List<User> findUsersByName(String name) {
        return userRepository.findByName(name);
    }

}
