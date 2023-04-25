package com.back2261.communityservice.infrastructure.repository;

import com.back2261.communityservice.infrastructure.entity.Community;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID> {}
