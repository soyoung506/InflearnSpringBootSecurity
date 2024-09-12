package com.edenbiz.inflearn.security1.config.oauth.provider;

import java.util.Map;

// naver 속성값
public class NaverUserInfo implements OAuth2UserInfo{
	
	private Map<String, Object> attributes;
	private String socialName;
	
	public NaverUserInfo(Map<String, Object> attributes, String socialName) {
		this.attributes = attributes;
		this.socialName = socialName;
	}

	@Override
	public String getProviderId() {
		return (String)attributes.get("id");
	}

	@Override
	public String getProvider() {
		return socialName;
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getName() {
		return (String)attributes.get("name");
	}

}
