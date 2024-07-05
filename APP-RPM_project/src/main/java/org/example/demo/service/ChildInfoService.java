package org.example.demo.service;

import jakarta.transaction.Transactional;
import org.example.demo.entity.ChildInfo;
import org.example.demo.entity.User;
import org.example.demo.repository.ChildInfoRepository;
import org.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildInfoService {

    private final ChildInfoRepository childInfoRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChildInfoService(ChildInfoRepository childInfoRepository, UserRepository userRepository) {
        this.childInfoRepository = childInfoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveChildInfo(ChildInfo childInfo, int userNo) {
        User user = userRepository.findById(userNo).orElseThrow(() -> new IllegalArgumentException("Invalid user No: " + userNo));
        childInfo.setUser(user);
        childInfoRepository.save(childInfo);
    }
}
