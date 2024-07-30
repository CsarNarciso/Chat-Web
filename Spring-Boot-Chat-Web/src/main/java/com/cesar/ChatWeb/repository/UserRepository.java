package com.cesar.ChatWeb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cesar.ChatWeb.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("SELECT u FROM User u WHERE u.name = :nameOrEmail OR u.email = :nameOrEmail")
	Optional<User> findByNameOrEmail(@Param("nameOrEmail") String nameOrEmail);

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.id = :id")
	Optional<User> findById(@Param("id") Long id);

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.name = :newName WHERE u.id = :id")
	void updateName(@Param("newName") String newName, @Param("id") Long id);

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.imageName = :newImageName WHERE u.id = :id")
	void updateImageName(@Param("newImageName") String newImageName, @Param("id") Long id);
}
