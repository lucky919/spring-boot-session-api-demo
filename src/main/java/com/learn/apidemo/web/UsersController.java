package com.learn.apidemo.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learn.apidemo.domain.UserEntity;
import com.learn.apidemo.dto.RegisterUserRequestModel;
import com.learn.apidemo.dto.RegisterUserResponseModel;
import com.learn.apidemo.service.ISessionService;
import com.learn.apidemo.service.IUserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class UsersController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISessionService sessionService;
	
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
	
	@PostMapping("/signout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	    	log.info("Logout in progress with auth - {}", auth.getPrincipal());
	    	String userId = (String)auth.getPrincipal();
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	        sessionService.deleteSession(userId);
	    } else {
	    	log.info("something went wrong with logout");
	    }
	    
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
	
}
