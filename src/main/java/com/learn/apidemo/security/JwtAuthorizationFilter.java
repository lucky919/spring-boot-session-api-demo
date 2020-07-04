package com.learn.apidemo.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.learn.apidemo.service.ISessionService;

import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private Environment env;
	
	private ISessionService sessionService;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, Environment env, ISessionService sessionService) {
		super(authenticationManager);
		this.env = env;
		this.sessionService = sessionService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(env.getProperty("authorization.header.token.name"));
		if (header == null || !header.startsWith(env.getProperty("authorization.header.token.prefix"))) {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken auth = getAuthentication(request, header);
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(request, response);
	}

	public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String authHeader) {
		if (authHeader == null)
			return null;
		
		String token = authHeader.replace(env.getProperty("authorization.header.token.prefix"), "");
		
		if (token != null) {
			String userId = Jwts.parser()
					.setSigningKey(env.getProperty("token.secret"))
					.parseClaimsJws(token).getBody()
					.getSubject();
			
			if (!sessionService.validateSession(userId)) {
				log.info("oops! session expired, returning 403");
				return null;
			}
			
			if (userId != null)
				return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

		}
		return null;
	}
}
