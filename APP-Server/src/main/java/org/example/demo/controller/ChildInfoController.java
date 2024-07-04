package org.example.demo.controller;

import org.example.demo.entity.ChildInfo;
import org.example.demo.repository.ChildInfoRepository;
import org.example.demo.service.ChildInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChildInfoController {

    private final ChildInfoService childInfoService;

    @Autowired
    public ChildInfoController(ChildInfoService childInfoService) {
        this.childInfoService = childInfoService;
    }

    @PostMapping("/childInfo")
    public ResponseEntity<Void> addChildInfo(@RequestBody ChildInfo childInfo, @RequestParam int userNo) {
        try {
            childInfoService.saveChildInfo(childInfo, userNo); // 자녀 정보 저장 서비스 메서드 호출
            return ResponseEntity.ok().build(); // HTTP 상태 코드 200 OK 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 상태 코드 500 Internal Server Error 반환
        }
    }

//    @GetMapping("/childInfo")
//    public List<ChildInfo> getAllChildInfo() {
//        return childInfoRepository.findAll();
//    }
}
