package com.cesar.UserAPI.repository;

import com.cesar.UserAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Long, User> {
}
