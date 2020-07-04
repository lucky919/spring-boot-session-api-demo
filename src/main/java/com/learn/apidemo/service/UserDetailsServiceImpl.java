package com.learn.apidemo.service;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.learn.apidemo.domain.UserEntity;
import com.learn.apidemo.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserDetailsServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepo.findByEmail(username);
		if (user == null)
			throw new UsernameNotFoundException(username);

		return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
	}

	@Override
	public UserEntity registerUser(UserEntity userEntity) {
		userEntity.setUserId(UUID.randomUUID().toString());
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

		userEntity = userRepo.save(userEntity);
		
		log.info("user registration process finished - {}", userEntity);
		return userEntity;
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		UserEntity user = userRepo.findByEmail(email);
		if (user == null)
			throw new UsernameNotFoundException(email);

		return user;
	}

}
