// src/main/java/com/example/demo/UserRepository.java
package org.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.demo.entity.User;
import org.springframework.stereotype.Repository;

public interface LoginRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
}
