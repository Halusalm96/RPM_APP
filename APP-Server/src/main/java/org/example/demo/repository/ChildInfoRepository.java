package org.example.demo.repository;

import org.example.demo.entity.ChildInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildInfoRepository extends JpaRepository<ChildInfo, Long> {
}
