package org.example.demo.service;

import org.example.demo.entity.User;
import org.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean deleteUser(int userNo) {
        Optional<User> userOptional = userRepository.findById(userNo);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return true;
        }
        return false;
    }
}
