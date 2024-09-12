package com.edenbiz.inflearn.security1.config.oauth.provider;

// OAuth2.0 제공자마다 응답해주는 속성값이 달라 공통된 속성값으로 변경하기 위한 인터페이스
public interface OAuth2UserInfo {
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
}
