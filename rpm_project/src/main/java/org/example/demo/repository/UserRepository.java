// src/main/java/com/example/demo/UserRepository.java
package org.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.demo.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
}