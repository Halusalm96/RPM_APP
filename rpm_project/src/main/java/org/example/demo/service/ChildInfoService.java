package org.example.demo.service;

// ChildInfoService.java

import jakarta.transaction.Transactional;
import org.example.demo.entity.ChildInfo;
import org.example.demo.repository.ChildInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildInfoService {

    @Autowired
    private ChildInfoRepository childInfoRepository;

    @Transactional
    public void saveChildInfo(ChildInfo childInfo, int userNo) {
        // userNo를 사용하여 해당 사용자의 DB 작업을 수행
        // 예시에서는 단순히 자녀 정보를 저장하는 것으로 가정
        childInfoRepository.save(childInfo);
    }
}
