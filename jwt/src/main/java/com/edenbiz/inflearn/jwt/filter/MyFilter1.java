package com.edenbiz.inflearn.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter1 implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		// 헤더의 Authorization의 값이 예시 토큰(ExToken)과 동일할 경우에만 Controller로 진입
		// 정상 로그인이 되면 jwt 토큰을 발급 -> 클라이언트의 요청마다 헤더의 Authorization의 값으로 jwt가 넘어오고, 해당 토큰이 내가 발급한 토큰인지만 검증
		if(req.getMethod().equals("POST")) {
			System.out.println("POST 요청됨");
			String headerAuth = req.getHeader("Authorization");
			System.out.println(headerAuth);
			System.out.println("필터1");
			
			if (headerAuth.equals("ExToken")) {
				chain.doFilter(req, res);
			}else {
				PrintWriter out = res.getWriter();
				out.println("인증 안 됨");
			}
		}else {
			System.out.println("Method = GET");
		}
	}

}
