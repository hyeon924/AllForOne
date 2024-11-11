package com.market.allForOneReview.domain.user.repository;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByEmail(String email);
}
