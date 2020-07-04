package com.learn.apidemo.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learn.apidemo.domain.SessionEntity;

public interface SessionRepository extends CrudRepository<SessionEntity, Long> {
	
	SessionEntity findByUserId(String userId);
	
	@Modifying
	@Query("update SessionEntity s set s.lastVisitTime = :currentTimeInMillis where s.userId = :userId")
	void updateLastVisited(String userId, long currentTimeInMillis);
}
