package org.memoryextension.java.samples.examMaker.repositories;

import org.memoryextension.java.samples.examMaker.pojos.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<UserAuthentication, String> {

	UserAuthentication findByLogin(String login );
	
}
