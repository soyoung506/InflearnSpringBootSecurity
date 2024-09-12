package com.edenbiz.inflearn.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edenbiz.inflearn.security1.model.User;
import com.edenbiz.inflearn.security1.repository.UserRepository;

// /login 요청이 들어오면 자동으로 UserDetailsService 타입의 loadUserByUsername 메소드가 실행
@Service
public class PrincipalDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository UserRepository;

	// 매개변수 username을 받기 위해 로그인폼에서 username에 해당하는 입력의 name을 항상! username으로 설정할 것 (그렇지 않으면 매칭이 안 됨)
	// username으로 user 정보 로드
	// Security Session(Security ContextHolder) > Authentication > UserDetails(PrincipalDetails)
	// 함수 종료 시, @AuthenticationPrincipal 어노테이션이 생성됨
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = UserRepository.findByUsername(username);
		if(userEntity != null) {
			// PrincipalDetails는 인터페이스인 UserDetails의 구현체
			return new PrincipalDetails(userEntity);
		}
		return null;
	}

}
