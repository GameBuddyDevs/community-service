package com.back2261.communityservice.infrastructure.repository;

import com.back2261.communityservice.infrastructure.entity.Avatars;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarsRepository extends JpaRepository<Avatars, UUID> {}
