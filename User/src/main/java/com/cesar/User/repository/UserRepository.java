package com.cesar.User.repository;

import com.cesar.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query("UPDATE User user SET user.profileImageUrl = :newUrl WHERE user.id = :id")
    void updateProfileImageUrl(@Param("id") Long id, @Param("newUrl") String newUrl);
    @Modifying
    @Query("UPDATE User user SET user.conversationsIds = :conversationsIds WHERE user.id = :id")
    void updateConversationsIds(@Param("id") Long id, @Param("conversationsIds") List<Long> conversationsIds);
}