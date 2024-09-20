package com.edenbiz.inflearn.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edenbiz.inflearn.jwt.model.User;


public interface UserRepository extends JpaRepository<User, Integer>{

	public User findByUsername(String username);
}
