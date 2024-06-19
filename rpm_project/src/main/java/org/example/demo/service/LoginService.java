package org.example.demo.service;

import org.example.demo.entity.User;
import org.example.demo.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final LoginRepository loginRepository;

    @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }


    // 사용자 조회 메서드
    public User findByUserId(String userId) {
        return loginRepository.findByUserId(userId);
    }

    // 로그인 처리 메서드
    public User login(String userId, String userPw) {
        User user = loginRepository.findByUserId(userId);
        if (user != null && user.getUserPw().equals(userPw)) {
            return user;
        }
        return null; // 로그인 실패 시 null 반환
    }
}
