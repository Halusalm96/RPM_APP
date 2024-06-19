package org.example.demo.controller;

import org.example.demo.entity.User;
import org.example.demo.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/update")
public class UpdateController {

    @Autowired
    private UpdateRepository updateRepository;

    // 사용자 정보 가져오기
    @GetMapping("/{userNo}")
    public ResponseEntity<User> getUser(@PathVariable int userNo) {
        Optional<User> updateOptional = updateRepository.findById(userNo);
        return updateOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 사용자 정보 수정
    @PutMapping("/{userNo}")
    public ResponseEntity<User> updateUser(@PathVariable int userNo, @RequestBody User updatedUser) {
        Optional<User> userOptional = updateRepository.findById(userNo);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // 업데이트 로직 적용
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setUserBirth(updatedUser.getUserBirth());
            existingUser.setUserEmail(updatedUser.getUserEmail());
            existingUser.setUserNumber(updatedUser.getUserNumber());
            updateRepository.save(existingUser); // 수정된 사용자 정보 저장
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
