package com.saumya.userservice.repository;

import com.saumya.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Atomic: only flips the flag if it's still false, so two concurrent
    // activation requests for the same account can't both "win" the race.
    @Modifying
    @Query("UPDATE User u SET u.mfaEnabled = true WHERE u.email = :email AND u.mfaEnabled = false")
    int enableMfaIfDisabled(@Param("email") String email);
}
