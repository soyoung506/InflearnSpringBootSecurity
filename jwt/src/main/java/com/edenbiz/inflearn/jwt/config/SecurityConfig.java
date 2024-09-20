package com.edenbiz.inflearn.jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.edenbiz.inflearn.jwt.config.jwt.JwtAuthenticationFilter;
import com.edenbiz.inflearn.jwt.config.jwt.JwtAuthorizationFilter;
import com.edenbiz.inflearn.jwt.filter.MyFilter1;
import com.edenbiz.inflearn.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final CorsFilter corsFilter;
	private final UserRepository userRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// securityFilterChain에서 특정 필터보다 우선적으로 또는 다음으로 새로운 필터를 실행시키고 싶을 경우
//		http.addFilterAfter(new MyFilter1(), BasicAuthenticationFilter.class);
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Session을 사용하지 않음
		.and()
		.addFilter(corsFilter)
		.formLogin().disable()  // Form 태그 로그인을 사용하지 않음
		.httpBasic().disable()  // Header에 Authorization의 값으로 id와 password를 전달하는 방법을 사용하지 않음
		.addFilter(new JwtAuthenticationFilter(authenticationManager()))  // WebSecurityConfigurerAdapter가 authenticationManager를 반환하는 메소드를 가지고 있음
		.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
		.authorizeRequests()
		.antMatchers("/api/v1/user/**")
		.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		.antMatchers("/api/v1/manager/**")
		.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		.antMatchers("/api/v1/admin/**")
		.access("hasRole('ROLE_ADMIN')")
		.anyRequest().permitAll();
	}
}
