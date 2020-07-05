package com.learn.apidemo.service;

public interface ISessionService {

	void updateSession(String userId);
	
	boolean validateSession(String userId);
	
	void deleteSession(String userId);
}
