package org.example.demo.repository;

import org.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpdateRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer userNo);
    // 기본적으로 JpaRepository가 제공하는 CRUD 기능을 사용할 수 있음
}
