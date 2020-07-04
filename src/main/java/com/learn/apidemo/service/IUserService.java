package com.learn.apidemo.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.learn.apidemo.domain.UserEntity;

public interface IUserService extends UserDetailsService {

	UserEntity registerUser(UserEntity userEntity);
	
	UserEntity getUserByEmail(String email);
	
}
