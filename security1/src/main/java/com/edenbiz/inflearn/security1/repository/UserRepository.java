package com.edenbiz.inflearn.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edenbiz.inflearn.security1.model.User;

// @Repository 어노테이션이 없어도 IoC가 가능. JpaRepository를 상속받았기 때문
public interface UserRepository extends JpaRepository<User, Integer>{
	// findBy 규칙(Query Methods) => findBy 뒤에 속성명 지정
	// select * from user where username = ?
	public User findByUsername(String username);
}