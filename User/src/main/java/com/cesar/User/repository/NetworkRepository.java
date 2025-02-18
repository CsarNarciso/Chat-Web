package com.cesar.User.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cesar.User.entity.Network;

@Repository
public interface NetworkRepository extends JpaRepository<Network, Long> {

    Network findByUserId(Long userId);

    @Query("SELECT n FROM Network n WHERE n.userId IN :userIds GROUP BY n.userId")
    List<Network> findByUserIds(@Param("userIds") List<Long> userIds);
}