// src/main/java/com/example/demo/UserController.java
package org.example.demo.controller;

import org.example.demo.repository.UserRepository;
import org.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/signup")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> signup(
            @RequestParam("id") String id,
            @RequestParam("pw") String pw,
            @RequestParam("name") String name,
            @RequestParam("birth") LocalDate birth,
            @RequestParam("email") String email,
            @RequestParam("number") String number) {

        // 데이터를 User 객체로 생성
        User newUser = new User(id, pw, name, birth, email, number);

        // 받은 데이터를 로그로 출력
        System.out.println("Received signup request: " + newUser);

        // 유효성 검사 등을 수행하고 유저를 저장하거나 에러를 처리합니다.
        userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}