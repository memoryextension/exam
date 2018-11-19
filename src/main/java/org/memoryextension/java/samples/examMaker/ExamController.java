package org.memoryextension.java.samples.examMaker;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;


import org.memoryextension.java.samples.examMaker.pojos.AppUser;

import org.memoryextension.java.samples.examMaker.pojos.Exam;
import org.memoryextension.java.samples.examMaker.pojos.ExamAnswer;
import org.memoryextension.java.samples.examMaker.repositories.ExamAnswerRepository;

import org.memoryextension.java.samples.examMaker.repositories.ExamRepository;
import org.memoryextension.java.samples.examMaker.repositories.UserRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/exam")
public class ExamController {
	
	private static final Logger log = LoggerFactory.getLogger(ExamController.class);
	
	private UserRepository userRepo;
	private ExamRepository examRepo;
	private ExamAnswerRepository examAnswerRepo;

	
	
	@Autowired
	public ExamController(UserRepository userRepo,ExamRepository examRepo,ExamAnswerRepository examAnswerRepo) {
	 this.userRepo = userRepo;
	 this.examRepo = examRepo;
	 this.examAnswerRepo=examAnswerRepo;
	}

	@GetMapping(value={"/"})
	@PreAuthorize("hasAnyRole('ROLE_user')")
	public String viewExamList(Model model){
		List<Exam> result =examRepo.findAll();
		model.addAttribute("examList",result );
		return "user_exam_list";
	}
	
	@GetMapping(value={"/{id}"})
	@PreAuthorize("hasAnyRole('ROLE_user')")
	public String viewExam(Model model,@PathVariable(value="id") final Long id,RedirectAttributes redirect) {
		Optional<Exam> exam = examRepo.findById(id);
		if(!exam.isPresent()){
			redirect.addFlashAttribute("globalMessage", "You can't access this exam.");
			return "redirect:/exam/";
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		AppUser user = userRepo.findByLogin(auth.getName());
		log.error(Boolean.toString(user!=null));
		log.error(exam.get().getTitle());
		
		// we load previous answers if they exist
		Optional<ExamAnswer> previousAnswers = examAnswerRepo.findByExamAndUser(exam.get(),user);
		ExamAnswer answer;
		if(previousAnswers.isPresent()) {
			 answer = previousAnswers.get();
		} else {
			log.error("Creating a new exam answer");
			answer = new ExamAnswer(exam.get(),user);
			answer.sizeAnswers(exam.get().getQuestions().size());
		}
		model.addAttribute("answer",answer );
		return "user_exam";
	}
	
	@PostMapping(value={"/{id}"})
	@PreAuthorize("hasAnyRole('ROLE_user')")
	public String saveResult(@ModelAttribute("examAnswer") @Valid ExamAnswer examAnswer, BindingResult result,
			@PathVariable(value="id") final Long id,Model model,RedirectAttributes redirect) {
		Optional<Exam> exam = examRepo.findById(id);
		if(!exam.isPresent() || !exam.get().getId().equals(examAnswer.getExam().getId()) ){
			redirect.addFlashAttribute("globalMessage", "You can't access this exam.");
			return "redirect:/exam/";
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		AppUser user = userRepo.findByLogin(auth.getName() );
		examAnswer.setUser(user);
		examAnswerRepo.save(examAnswer);
		return "redirect:/exam/";
	}
	
	
}
