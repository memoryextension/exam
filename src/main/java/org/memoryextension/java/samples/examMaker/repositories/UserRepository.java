package org.memoryextension.java.samples.examMaker.repositories;

import java.util.List;

import org.memoryextension.java.samples.examMaker.pojos.AppUser;
import org.memoryextension.java.samples.examMaker.pojos.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
	AppUser findByLogin(String login );
	AppUser findByEmail(String email);
    AppUser findByUserRolesAndEmail(UserRole role, String email);
	List<AppUser> findByLoginStartsWithIgnoreCase(String login );
	List<AppUser> findByUserRoles(UserRole role );
}
