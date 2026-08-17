package com.myapp.taskmanager.repository;

import com.myapp.taskmanager.entity.RefreshToken;
import com.myapp.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying // necesario para queries de modificacion custom
    int deleteByUser(User user);
}
