package com.learn.apidemo.web;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learn.apidemo.domain.UserEntity;
import com.learn.apidemo.dto.RegisterUserRequestModel;
import com.learn.apidemo.dto.RegisterUserResponseModel;
import com.learn.apidemo.service.IUserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class UsersController {

	@Autowired
	private IUserService userService;
	
	@GetMapping("/dummy")
	public String status() {
		log.info("inside protected method dummy");
		return "Just showing the restricted dummy page";
	}
	
	@PostMapping("/signup")
	public ResponseEntity<RegisterUserResponseModel> registerUser(@Valid @RequestBody RegisterUserRequestModel registerUserModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity user = mapper.map(registerUserModel, UserEntity.class);
		UserEntity createdUser = userService.registerUser(user);
		
		RegisterUserResponseModel model = mapper.map(createdUser, RegisterUserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(model);
	}
	
	
	
}
