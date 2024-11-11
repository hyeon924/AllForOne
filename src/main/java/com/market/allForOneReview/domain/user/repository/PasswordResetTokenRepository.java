package com.market.allForOneReview.domain.user.repository;

import com.market.allForOneReview.domain.user.entity.PasswordResetToken;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(SiteUser user);

    @Query("SELECT u FROM SiteUser u WHERE u.email = :email")
    Optional<SiteUser> findUserByEmail(@Param("email") String email);
}
