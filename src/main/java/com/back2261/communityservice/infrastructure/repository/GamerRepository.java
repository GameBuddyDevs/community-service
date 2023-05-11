package com.back2261.communityservice.infrastructure.repository;

import com.back2261.communityservice.infrastructure.entity.Gamer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, String> {

    Optional<Gamer> findByEmail(String email);
}
