package com.edenbiz.inflearn.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.edenbiz.inflearn.security1.config.auth.PrincipalDetails;
import com.edenbiz.inflearn.security1.model.User;
import com.edenbiz.inflearn.security1.repository.UserRepository;

@Controller
public class IndexController {
	
	@Autowired
	private UserRepository UserRepository;
	@Autowired
	private BCryptPasswordEncoder BCryptPasswordEncoder;
	
	// 일반 회원가입 사용자
	@GetMapping("/test/login")
	// User 정보 추출 방법 1: Authentication을 매개변수로 받아 UserDetails(PrincipalDetails)로 다운캐스팅
	// User 정보 추출 방법 2: @AuthenticationPrincipal 어노테이션을 사용하여 UserDetails 호출
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
		// 로그인이 완료되며 생성된 시큐리티 세션에 사용자 정보를 담은 Authentication 객체가 저장되고 해당 객체를 매개변수로 불러올 수 있음
		// 만약 사용자 정보가 없는, 로그인 되지 않은 상태에서 Authentication 객체 또는 UserDetails를 호출하면 에러 발생 (because "authentication" is null)
		// SecurityConfig에서 authenticated() 설정이 된 API에서만 Authentication 객체 또는 UserDetails 객체를 호출
		
		// Authentication 객체에서 PrincipalDetails 객체로 캐스팅 -> User 정보 추출 가능
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("/test/login =========");
		// PrincipalDetails 클래스는 Data 어노테이션을 사용하여 User Getter를 사용할 수 있음
		System.out.println("authentication: " + principalDetails.getUser());
		// @AuthenticationPrincipal 어노테이션은 UserDetails를 호출할 수 있으며, PrincipalDetails 역시 UserDetails를 상속받기 때문에 호출 가능
		System.out.println("userDetails: " + userDetails.getUser());
		return "세션 정보 확인";
	}
	
	// oauth2 회원가입 사용자
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) { 
		// 일반 회원가입 사용자와 다르게 소셜로그인 사용자는 OAuth2User 객체로 캐스팅
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("/test/oauth/login =========");
		System.out.println("authentication: " + oauth2User.getAttributes());
		System.out.println("oauth2User: " + oauth.getAttributes());
		return "oauth2User 세션 정보 확인";
	}

	@GetMapping({"","/"})
	public String index() {
		return "index";
	}
	
	// 일반 또는 소셜 로그인에 따라 유저정보 추출 방식이 다름 -> 2개의 동일한 API 생성 필요
	// 이러한 문제를 해결하기 위해 UserDetails와 OAuth2User 인터페이스를 구현하는 하나의 클래스 생성 => PrincipalDetails
	// 유저 정보가 필요한 모든 API에서 PrincipalDetails를 호출하면 동일한 방법으로 어떤 사용자든 유저정보 추출 가능
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("PrincipalDetails: "+ principalDetails.getUser());
		System.out.println("PrincipalDetails_oauthName: "+ principalDetails.getName());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링시큐리티와 동일한 URI로 인해 해당 페이지 출력X -> securityConfig 설정으로 노출
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		// 비밀번호 암호화를 하지 않으면 시큐리티로 로그인할 수 없음
		String rawPassword = user.getPassword();
		String encPassword = BCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		UserRepository.save(user);
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")  // 권한요청이 하나일 경우
	@GetMapping("/info") 
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	// @PostAuthorize("hasRole('ROLE_ADMIN')")  // 해당 메소드 실행 후 권한 확인
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")  // 권한요청이 두개 이상일 경우 (hasRole함수 필요) / 해당 메소드 실행 전 권환 확인
	@GetMapping("/data") 
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
