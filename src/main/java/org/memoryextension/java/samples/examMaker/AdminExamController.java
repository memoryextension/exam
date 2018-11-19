package org.memoryextension.java.samples.examMaker;

import java.util.Optional;
import javax.validation.Valid;

import org.memoryextension.java.samples.examMaker.pojos.Exam;
import org.memoryextension.java.samples.examMaker.repositories.ExamRepository;
import org.memoryextension.java.samples.examMaker.utils.forms.QuestionForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/exam")
public class AdminExamController {

	private static final Logger log = LoggerFactory.getLogger(AdminExamController.class);

	private ExamRepository examRepo;
	
	@Autowired
	public AdminExamController(ExamRepository examRepo) {
		this.examRepo = examRepo;

	}
	
	// auto trim the fields
	// per http://stackoverflow.com/questions/2691667/can-spring-mvc-trim-all-strings-obtained-from-forms
	@InitBinder
    public void initBinder ( WebDataBinder binder )
    {
        StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringtrimmer);
    }	
	
	
	
	/**************************************************************/
	/**                                                           */
	/**                  to manage exam                           */
	/**                                                           */
	/**************************************************************/
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/all")
	public String exams(Model model){
		model.addAttribute("exams",examRepo.findAll());	
		return "admin_exams";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/{id}/edit")
	public String examEditForm(Model model,@PathVariable(value="id") final Long id){
		model.addAttribute("exam",examRepo.findById(id).get());
		return "admin_exam_form";
	}
	
	
	private String saveExam(Exam exam, BindingResult result,Model model,RedirectAttributes redirect,boolean addMore) {
		if (result.hasErrors()) {
			return "admin_exam_form";
        }
		
		examRepo.save(exam);
		if(addMore){ return "redirect:/admin/exam/add"; }
		redirect.addFlashAttribute("globalMessage", "exam '"+exam.getTitle()+"' successfully saved.");
		if(addMore) {
			return examAddForm(model); 
		}
        return "redirect:/admin/exam/all";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/add")
	public String examAddForm(Model model){
		model.addAttribute("exam",new Exam());
		return "admin_exam_form";
	}
	
	
	/* the 4 buttons for edit/save/delete/cancel */
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/add", method=RequestMethod.POST, params={"save", "!cancel","!del","!saveAdd"})
	public String examSaveOnce(@ModelAttribute("exam") @Valid Exam exam, BindingResult result,
			Model model,RedirectAttributes redirect) {
		return saveExam(exam,result,model,redirect,false);
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/add", method=RequestMethod.POST, params={"!save", "!cancel","!del","saveAdd"})
	public String examSaveAdd(@ModelAttribute("exam") @Valid Exam exam, BindingResult result,
			Model model,RedirectAttributes redirect) {
		return saveExam(exam,result,model,redirect,true);
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/add", method=RequestMethod.POST, params={"!save", "cancel","!del","!saveAdd"})
	public String examEditCancel(RedirectAttributes redirect){
		redirect.addFlashAttribute("globalMessage", "Editing cancelled");
        return "redirect:/admin/exam/all";
	}
	
	public String examDelete(Long id,RedirectAttributes redirect) {
		Optional<Exam> instanceToDelete = examRepo.findById(id);
		if(instanceToDelete.isPresent()){
			examRepo.delete(instanceToDelete.get());
			redirect.addFlashAttribute("globalMessage", instanceToDelete.get().getTitle()+" deleted.");
		}
        return "redirect:/admin/exam/all";
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/add", method=RequestMethod.POST, params={"!save", "!cancel","del","!saveAdd"})
	public String examDeleteViaEdit(@ModelAttribute("user") @Valid Exam exam, BindingResult result,Model model,RedirectAttributes redirect) {
		if (result.hasErrors()) { return "admin_exam_form"; }
		return examDelete(exam.getId(),redirect);
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@PostMapping(value="/{id}/delete")
	public String examDirectDelete(@PathVariable(value="id") final Long examId, RedirectAttributes redirect) {
		return examDelete(examId,redirect);
	}
	
	
	/**************************************************************/
	/**                                                           */
	/**                  to manage exam  questions                */
	/**                                                           */
	/**************************************************************/
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/{id}/questions")
	public String showExamQuestions(Model model,@PathVariable(value="id") final Long id,RedirectAttributes redirect){
		
		Optional<Exam> exam = examRepo.findById(id);
		
		if(!exam.isPresent()) {
			redirect.addFlashAttribute("globalMessage", "You are not authorized to edit this Exam");
			return "redirect:/admin/exam/all";
		}
		model.addAttribute("exam",exam.get());
		model.addAttribute("questions",exam.get().getQuestions() );
		return "admin_exam_questions";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/{id}/question/add")
	public String addQuestion(Model model,@PathVariable(value="id") final Long id,RedirectAttributes redirect){
		Optional<Exam> exam = examRepo.findById(id);
		if(!exam.isPresent()) {
			redirect.addFlashAttribute("globalMessage", "You are not authorized to edit this Exam");
			return "redirect:/admin/exam/all";
		}
		
		model.addAttribute("questionForm",new QuestionForm(exam.get().getId(),exam.get().getTitle()));
		return "admin_exam_question_form";
	}
	
	public String saveQuestion(QuestionForm examFile,BindingResult result, Model model,RedirectAttributes redirect,boolean addMore) {
		if (result.hasErrors()) {
			return "admin_exam_question_form";
        }
		Optional<Exam> exam = examRepo.findById(examFile.getExamId());
		if(!exam.isPresent()) {
			redirect.addFlashAttribute("globalMessage", "You are not authorized to edit this Exam");
			return "redirect:/admin/exam/all";
		}
		exam.get().addQuestion(examFile.getQuestion());
		examRepo.save(exam.get());
		redirect.addFlashAttribute("globalMessage", "Question added");
		
		return "redirect:/admin/exam/"+Long.toString(exam.get().getId())+"/questions";
		
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/{id}/question/add", method=RequestMethod.POST, params={"save", "!cancel","!del","!saveAdd"})
	public String examFileSaveOnce(@ModelAttribute("file") @Valid QuestionForm question, BindingResult result,
			Model model,RedirectAttributes redirect) {
		return saveQuestion(question,result,model,redirect,false);
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/{id}/question/add", method=RequestMethod.POST, params={"!save", "!cancel","!del","saveAdd"})
	public String examFileSaveAdd(@ModelAttribute("file") @Valid QuestionForm question, BindingResult result,
			Model model,RedirectAttributes redirect) {
		return saveQuestion(question,result,model,redirect,true);
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/{id}/question/add", method=RequestMethod.POST, params={"!save", "cancel","!del","!saveAdd"})
	public String examFileEditCancel(RedirectAttributes redirect){
		redirect.addFlashAttribute("globalMessage", "Editing cancelled");
        return "redirect:/admin/exam/all";
	}
	
	
	
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@PostMapping("/{id}/question/{qid}/delete")
	public String deleteQuestion(Model model,@PathVariable(value="id") final Long id,@PathVariable(value="qid") final Integer qid,RedirectAttributes redirect){
		Optional<Exam> exam = examRepo.findById(id);
		
		if(!exam.isPresent()) {
			redirect.addFlashAttribute("globalMessage", "You are not authorized to edit this Exam");
			return "redirect:/admin/exam/all";
		}
		exam.get().getQuestions().remove(qid.intValue());
		examRepo.save(exam.get());
		
		model.addAttribute("exam",exam.get());
		model.addAttribute("questions",exam.get().getQuestions() );
		return "admin_exam_questions";
	}
	
}
