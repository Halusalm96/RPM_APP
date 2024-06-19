package org.example.demo.service;

import org.example.demo.entity.User;
import org.example.demo.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateService {

    private final UpdateRepository updateRepository;

    @Autowired
    public UpdateService(UpdateRepository updateRepository) {
        this.updateRepository = updateRepository;
    }

    // 사용자 정보 가져오기
    public Optional<User> getUserById(Integer userId) {
        return updateRepository.findById(userId);
    }

    // 사용자 정보 수정
    public User updateUser(Integer userId, User updatedUser) {
        Optional<User> userOptional = updateRepository.findById(userId);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // 업데이트 로직 적용
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setUserBirth(updatedUser.getUserBirth());
            existingUser.setUserEmail(updatedUser.getUserEmail());
            existingUser.setUserNumber(updatedUser.getUserNumber());
            return updateRepository.save(existingUser); // 수정된 사용자 정보 저장 및 반환
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다."); // 예외 처리 혹은 적절한 처리 방법을 선택
        }
    }
}
