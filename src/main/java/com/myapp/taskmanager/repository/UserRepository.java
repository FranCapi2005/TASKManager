package com.myapp.taskmanager.repository;

import com.myapp.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // Spring genera automaticamente "SELECT COUNT(*) > 0 WHEN email = ?
}
