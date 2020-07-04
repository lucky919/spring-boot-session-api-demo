package com.learn.apidemo.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.learn.apidemo.domain.SessionEntity;
import com.learn.apidemo.repository.SessionRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SessionServiceImpl implements ISessionService {

	@Autowired
	private SessionRepository sessionRepo;

	@Value("${token.session.timeout}")
	private long sessionTimeoutInMillis;

	@Override
	public void updateSession(String userId) {
		SessionEntity session = null;
		if (userId != null) {
			session = sessionRepo.findByUserId(userId);
		}

		if (session == null) {
			session = new SessionEntity();
			session.setUserId(userId);
			session.setSessionId(UUID.randomUUID().toString());
			session.setCreationTime(new Date());
		}
		updateSession(session);
	}

	@Override
	public boolean validateSession(String userId) {
		log.info("validting the session for current user : {}", userId);
		if (userId != null) {
			SessionEntity session = sessionRepo.findByUserId(userId);
			if (session != null) {
				boolean isValid = System.currentTimeMillis()
						- (session.getLastVisitTime().getTime() + sessionTimeoutInMillis) <= 0;
				if (isValid) {
					updateSession(session);
				}
				return isValid;
			}
		}
		return false;
	}
	
	private void updateSession(SessionEntity session) {
		session.setLastVisitTime(new Date());
		sessionRepo.save(session);
		log.info("finished updating the session");
	}

}
