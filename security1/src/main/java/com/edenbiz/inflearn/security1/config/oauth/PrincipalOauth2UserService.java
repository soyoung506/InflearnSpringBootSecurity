package com.edenbiz.inflearn.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.edenbiz.inflearn.security1.config.auth.PrincipalDetails;
import com.edenbiz.inflearn.security1.config.oauth.provider.GoogleUserInfo;
import com.edenbiz.inflearn.security1.config.oauth.provider.NaverUserInfo;
import com.edenbiz.inflearn.security1.config.oauth.provider.OAuth2UserInfo;
import com.edenbiz.inflearn.security1.model.User;
import com.edenbiz.inflearn.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	// 소셜 로그인으로부터 받은 userRequest(사용자 정보) 데이터에 대한 후처리 과정
	// userRequest는 로그인 완료 후 code -> accessToken
	// 함수 종료 시, @AuthenticationPrincipal 어노테이션이 생성됨
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		// 구글 로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code 리턴 (oauth client 랴이브러리) -> AccessToken 요청 -> userRequest 정보 및 회원 프로필 확인
		System.out.println("OAuth2AccessTokenValue:"+ userRequest.getAccessToken().getTokenValue());
		System.out.println("OAuth2AccessTokenScopes:"+ userRequest.getAccessToken().getScopes());
		System.out.println("UserNameAttributeName: "+ userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
				.getUserNameAttributeName());
		
		// 사용자 정보 조회 객체로 변환
		OAuth2User oauth2User = super.loadUser(userRequest);
		// code를 통해 구성한 정보 (ex. 어떤 Oauth에 로그인 했는지 확인)
		System.out.println("userRequest: "+ userRequest.getClientRegistration());
		// token을 통해 응답받은 사용자 정보
		System.out.println("oauth2User: "+ oauth2User);
		
		return processOAuth2User(userRequest, oauth2User);
	}
	
	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
		OAuth2UserInfo oauth2UserInfo = null;
		String socialName = userRequest.getClientRegistration().getRegistrationId();
		if(socialName.equals("google")) {
			System.out.println("구글 계정으로 로그인");
			oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes(), socialName);
		}else if (socialName.equals("naver")) {
			System.out.println("네이버 계정으로 로그인");
			// 네이버의 경우 attributes 속성이 response를 키로 하여 해시맵 구조로 한 번 더 감싸 있기 때문에 get() 함수를 통해 response 키값만 불러올 수 있도록 설정
			oauth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"), socialName);
		}else {
			System.out.println("구글과 네이버만 지원");
		}
		
		// userRequest에서 받은 사용자 정보에서 원하는 정보만 추출하여 강제 회원가입 진행
		String username = oauth2UserInfo.getProvider() + "_" + oauth2UserInfo.getProviderId();
		String password = bCryptPasswordEncoder.encode("아무거나");
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			System.out.println(socialName+" 계정으로 회원가입 되었습니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(oauth2UserInfo.getEmail())
					.role("ROLE_USER")
					.provider(oauth2UserInfo.getProvider())
					.providerId(oauth2UserInfo.getProviderId())
					.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("이미 "+socialName+" 계정으로 회원가입을 진행했습니다.");
		}

		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
