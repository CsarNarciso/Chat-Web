package com.cesar.Social.repository;

import com.cesar.Social.entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NetworkRepository extends JpaRepository<Network, Long> {

    Network findByUserId(Long userId);

    @Query("SELECT n FROM Network n WHERE n.userId IN :userIds GROUP BY n.userId")
    List<Network> findByUserIds(@Param("userIds") List<Long> userIds);
}