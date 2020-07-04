package com.learn.apidemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.learn.apidemo.service.ISessionService;
import com.learn.apidemo.service.IUserService;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private IUserService userDetailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ISessionService sessionService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().
			and().csrf().disable()
			.authorizeRequests()
			.antMatchers("/signup", "/h2-console/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilter(new JwtAuthenticationFilter(authenticationManager(), userDetailService, env, sessionService))
			.addFilter(new JwtAuthorizationFilter(authenticationManager(), env, sessionService))
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.headers().frameOptions().disable();
	}

}
