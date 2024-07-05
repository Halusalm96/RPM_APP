// src/main/java/com/example/demo/UserController.java
package org.example.demo.controller;

import org.example.demo.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginRepository loginRepository;

    @PostMapping
    public ResponseEntity<String> login(
            @RequestParam("id") String id,
            @RequestParam("pw") String pw) {

        // 로그에 요청 파라미터 출력
        logger.debug("Received login request - ID: {}, PW: {}", id, pw);

        // 사용자 ID로 사용자 조회
        User user = loginRepository.findByUserId(id);

        if (user == null) {
            // 사용자가 존재하지 않는 경우
            return ResponseEntity.badRequest().body("사용자가 존재하지 않습니다.");
        }

        // 비밀번호 일치 여부 확인
        if (!user.getUserPw().equals(pw)) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공
        return ResponseEntity.ok("Login successful! UserNo: " + user.getUserNo());
    }
}
