package com.edenbiz.inflearn.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.edenbiz.inflearn.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // secured 어노테이션 활성화, preAuthorize/postAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean  // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()  // 접근 권한 설정
		.antMatchers("/user/**").authenticated()  // 인증 필요
		.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // admin 또는 manager 권한 필요
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")  // admin 권한 필요
		.anyRequest().permitAll()  // 그 외 모든 접근 허용
		.and()
		.formLogin()
		.loginPage("/loginForm")  // security가 작동하도록 로그인 페이지로 포워딩
		.loginProcessingUrl("/login")  // login 주소가 호출되면 스프링 시큐리티가 대신 로그인을 진행 in UserDetailsService -> login API 생성 필요XX
		.defaultSuccessUrl("/")  // loginForm을 호출 후 완료되면 기본 URL("/")로 넘어가지만, 다른 호출에서 loginForm으로 이동 시 로그인이 완료되면 이전 호출로 리턴
		.and()
		.oauth2Login()
		.loginPage("/loginForm")
		.userInfoEndpoint()
		.userService(principalOauth2UserService);  
		// 구글 로그인이 완료되면 엑세스토큰과 사용자프로필정보를 얻음 -> 후처리 필요 in PrincipalOauth2UserService
		// oauth2 로그인 과정: 코드받기(인증) -> 엑세스토큰(권한) -> 얻은 사용자프로필 정보를 토대로 회원가입 자동 진행
	}

	
}
