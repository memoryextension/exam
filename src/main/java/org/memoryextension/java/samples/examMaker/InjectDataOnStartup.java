package org.memoryextension.java.samples.examMaker;

import java.util.ArrayList;
import java.util.List;

import org.memoryextension.java.samples.examMaker.pojos.AppUser;

import org.memoryextension.java.samples.examMaker.pojos.Exam;
import org.memoryextension.java.samples.examMaker.pojos.UserAuthentication;
import org.memoryextension.java.samples.examMaker.pojos.UserRole;
import org.memoryextension.java.samples.examMaker.repositories.AuthenticationRepository;
import org.memoryextension.java.samples.examMaker.repositories.ExamRepository;
import org.memoryextension.java.samples.examMaker.repositories.RoleRepository;
import org.memoryextension.java.samples.examMaker.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class InjectDataOnStartup implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(InjectDataOnStartup.class);
	
	private RoleRepository roleRepo;
	private UserRepository userRepo;
	private AuthenticationRepository internalAuthRepo;
	private ExamRepository examRepo;
	
	@Autowired
	public InjectDataOnStartup(RoleRepository roleRepo,
							UserRepository userRepo,
							AuthenticationRepository authRepo,
							ExamRepository examRepo) {
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
		this.internalAuthRepo = authRepo;
		this.examRepo = examRepo;
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		UserRole userRole = new UserRole("user","aka ESCAP onboarding team");
		roleRepo.save(userRole);
		UserRole adminRole = new UserRole("admin","Can edit account and permission, and see everything");
		roleRepo.save(adminRole);
		
		AppUser localAdmin = new AppUser("int_admin","Internal admin");
		localAdmin.addRole(adminRole);
		localAdmin.addRole(userRole);
		localAdmin.setEmail("admin@example.org");
		
		internalAuthRepo.save(new UserAuthentication("int_admin","VeryUnstrongPassword"));
		
		userRepo.save(localAdmin);
		Exam exam = new Exam();
		exam.setTitle("Sample exam");
		List<String> q = new ArrayList<String>();
		q.add("What's the color of water?");
		q.add("What time is it?");
		exam.setQuestions(q);
		
		examRepo.save(exam);
		
		
		
	}

}
 