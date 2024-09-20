package com.edenbiz.inflearn.jwt.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.edenbiz.inflearn.jwt.config.auth.PrincipalDetails;
import com.edenbiz.inflearn.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("로그인 시도중");
		try {
//			BufferedReader br = request.getReader();
//			String input = null;
//			while((input = br.readLine()) != null) {
//				System.out.println(input);
//			}
			
			// 1. username, password 받아서 UsernamePasswordAuthenticationToken 객체 생성
			ObjectMapper om = new ObjectMapper();  // Json 데이터를 자바 Object로 파싱해주는 클래스
			User user = om.readValue(request.getInputStream(), User.class);  // InputStream으로 읽어온 데이터를 User 객체로 변환
			System.out.println(user);
			UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			// 2. authenticationManager로 로그인 시도 -> UserDetailsService(PrincipalDetailsService) 호출 및 loadUserByUsername() 함수 실행
			// 아이디 비번이 일치하면 Authentication객체에 사용자 정보가 담김
			// 아이디 비번이 일치하지 않아 null값이 전달되면 시큐리티 종료 (컨트롤러 실행 X)
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			System.out.println(principalDetails.getUser().getUsername());  // 정상 출력 시 로그인 완료 상태
			
			// 3. Authentication(PrincipalDetails) 객체를 세션에 저장 (권한 관리를 위해)
			return authentication;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// 4. JWT 토큰을 발급 및 응답
	// attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수 실행
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨(사용자 인증 완료)");		
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		String jwtToken = JWT.create()
				.withSubject("JWT 토큰")  // 토큰 이름
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))  // 토큰 만료 시간 (현재시간 + 설정기간) (1000 = 1초)
				.withClaim("id", principalDetails.getUser().getId())  // 토큰에 추가할 사용자 정보
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));  // 비밀키 (HS512 알고리즘 사용)
		
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
	}
}
