package com.cesar.Social.repository;

import com.cesar.Social.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    Relationship findByUserId(Long userId);

    @Query("SELECT r FROM Relation r WHERE r.userId IN :usersIds")
    List<Relationship> findByUsersIds(@Param("usersIds") List<Long> usersIds);
}