package com.learn.apidemo.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.apidemo.domain.UserEntity;
import com.learn.apidemo.dto.LoginRequestModel;
import com.learn.apidemo.service.ISessionService;
import com.learn.apidemo.service.IUserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authManager;
	
	private IUserService userService;
	
	private Environment env;
	
	private ISessionService sessionService;
		
	public JwtAuthenticationFilter(AuthenticationManager authManager, IUserService userService, Environment env, ISessionService sessionService) {
		this.authManager = authManager;
		this.userService = userService;
		this.env = env;
		this.sessionService = sessionService;
		setFilterProcessesUrl(env.getProperty("login.url.path"));
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			log.info("AuthenticationFilter inside attemptAuth");
			LoginRequestModel credentials = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
			return authManager.authenticate(
					new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword(), Collections.emptyList())
					);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String username = ((User)authResult.getPrincipal()).getUsername();
		log.info("successful authentication with username : {}", username);
		
		UserEntity user = userService.getUserByEmail(username);
		
		String token = Jwts.builder()
				.setSubject(user.getUserId())
				.setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
				.signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
				.compact();
		
		sessionService.updateSession(user.getUserId());
		
		response.addHeader("token", token);
		response.addHeader("userId", user.getUserId());
	}
}
