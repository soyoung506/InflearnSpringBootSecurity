package com.edenbiz.inflearn.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.edenbiz.inflearn.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 로그인 진행
// 로그인이 완료되면 시큐리티 session을 생성 (Security ContextHolder)
// Security ContextHolder는 Authentication 객체만 저장
// Authentication은 User(UserDetails 객체 (PrincipalDetails가 UserDetails를 구현함)) 정보를 포함

@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user;
	private Map<String, Object> attributes;
	
	// 일반 로그인 생성자
	public PrincipalDetails(User user) {
		this.user=user;
	}
	
	// 소셜 로그인 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user=user;
		this.attributes=attributes;
	}
	
	// 해당 유저의 권한 리턴
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 반환 타입에 따라 Collection객체 생성
		Collection<GrantedAuthority> collect = new ArrayList<>();  // ArrayList는 Collection의 자식
		// Collection의 타입에 따라 GrantedAuthority 인스턴스 생성 및 추가
		collect.add(new GrantedAuthority() {
			// GrantedAuthority 인터페이스의 getAuthority() 메소드 오버라이딩으로 user 객체의 Role 반환
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}
	
	// 해당 유저의 비밀번호 리턴
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	// 해당 유저의 이름 리턴
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정 잠금 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 계정 비밀번호 사용기한 만료 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정 활성화 여부
	@Override
	public boolean isEnabled() {
		// Ex. 로그인 기간이 1년이 넘었을 경우 비활성화 (false)
		// 현재시간 - 최종로그인시간 = 1 이상일 경우
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		String name = (String)attributes.get("name");
		if(name == null) {
			Map<String, Object> responseAttributes = (Map<String, Object>) attributes.get("response");
			name = (String)responseAttributes.get("name");
		}
		return name;
	}

}
