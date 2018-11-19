package org.memoryextension.java.samples.examMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.memoryextension.java.samples.examMaker.pojos.AppUser;

import org.memoryextension.java.samples.examMaker.pojos.UserAuthentication;
import org.memoryextension.java.samples.examMaker.pojos.UserRole;
import org.memoryextension.java.samples.examMaker.repositories.AuthenticationRepository;


import org.memoryextension.java.samples.examMaker.repositories.RoleRepository;
import org.memoryextension.java.samples.examMaker.repositories.UserRepository;
import org.memoryextension.java.samples.examMaker.utils.forms.ImpersonateForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	
	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private  UserRepository userRepo;
	@Autowired
	private AuthenticationRepository authRepo;
	@Autowired
	private  RoleRepository roleRepo;

	
	// auto trim the fields
	// per http://stackoverflow.com/questions/2691667/can-spring-mvc-trim-all-strings-obtained-from-forms
	@InitBinder
    public void initBinder ( WebDataBinder binder )
    {
        StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringtrimmer);
    }

	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping("/")
	public String admin(Model model){
		return "admin_menu";
	}
	
	
	
	
	/********************************************************/
	/* to manage user                                       */
	/********************************************************/
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping("/users")
    public String users(Model model) {
		model.addAttribute("users",userRepo.findAll());
		return "admin_users";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/user/add")
    public String userForm(Model model) {
		AppUser user = new AppUser();
		
		// everyone is at least a user or an admin - adding default 'user' role
		user.addRole(roleRepo.findByNameStartsWithIgnoreCase("user"));
		model.addAttribute("user",user);
		
		model.addAttribute("roles",roleRepo.findAll());
		

		return "admin_user_form"; 
		
	}
	private String genericSaveUser(AppUser user, BindingResult result,Model model,RedirectAttributes redirect,boolean addMore){
		
		if (result.hasErrors() || ( user.getId()==null && user.getClearPassword()==null) ) {
			//todo add error message when password is empty
			model.addAttribute("roles",roleRepo.findAll());
			return "admin_user_form";
        }
		
		// saving auth 
		// password null means user was edited but password did not change
		if(user.getClearPassword()!=null) {
			authRepo.save(new UserAuthentication(user.getLogin(),user.getClearPassword()));
		}
		userRepo.save(user);
		if(addMore){ return "redirect:/admin/user/add"; }
		redirect.addFlashAttribute("globalMessage", user.getLogin()+" successfully saved.");
        return "redirect:/admin/users";
	}
	
	
	// BindingResult must be right after object
	// http://stackoverflow.com/questions/27689901/spring-mvc-thymeleaf-form-validation-and-error-messages explains why ModelAttribute
	// Trying multiple actions: http://stackoverflow.com/questions/8954426/spring-mvc-multiple-submit-button-to-a-form
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/user/add", method=RequestMethod.POST, params={"save", "!cancel","!del"})
	public String userSave(@ModelAttribute("user") @Valid AppUser user, BindingResult result,
			Model model,RedirectAttributes redirect) {
		return genericSaveUser(user,result,model,redirect,false);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/user/add", method=RequestMethod.POST, params={"saveAdd"})
	public String userSaveAdd(@ModelAttribute("user") @Valid AppUser user, BindingResult result,
			Model model) {
		return genericSaveUser(user,result,model,null,true);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/user/add", method=RequestMethod.POST, params={"!save", "cancel","!del"})
	public String userEditCancel(RedirectAttributes redirect){
		redirect.addFlashAttribute("globalMessage", "Editing cancelled");
        return "redirect:/admin/users";
	}
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/user/add", method=RequestMethod.POST, params={"!save", "!cancel","del"})
	public String userDelete(@ModelAttribute("user") @Valid AppUser user, BindingResult result,Model model,RedirectAttributes redirect) {
		if (result.hasErrors()) {
			model.addAttribute("roles",roleRepo.findAll());
			return "admin_user_form";
        }
		String username=user.getLogin();
		
		authRepo.delete(new UserAuthentication(user.getLogin(),""));

		userRepo.delete(user);
		redirect.addFlashAttribute("globalMessage", username+" deleted.");
        return "redirect:/admin/users";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/user/{id}/edit")
    public String userEdit(Model model, @PathVariable(value="id") final Long userId,RedirectAttributes redirect) {
		Optional<AppUser> user = userRepo.findById(userId);
		if(user.isPresent()) {
			model.addAttribute("user",user.get());
			model.addAttribute("email",user.get().getEmail());
			model.addAttribute("roles",roleRepo.findAll());
			return "admin_user_form";
		}
		
		redirect.addFlashAttribute("globalMessage","Invalid user");
        return "redirect:/admin/users";
    	
    }



	/**************************************************************/
	/**                                                           */
	/**                  to impersonate                           */
	/**                                                           */
	/**************************************************************/
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@GetMapping("/impersonate")
	public String impersonate(Model model,RedirectAttributes redirect) {
		ImpersonateForm form = new ImpersonateForm();
		model.addAttribute("impersonation",form);
		model.addAttribute("roles",roleRepo.findAll());
		model.addAttribute("users",userRepo.findAll());
		
		return "admin_impersonate_form";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_admin')")
	@RequestMapping(value="/impersonate", method=RequestMethod.POST, params={"save", "!cancel"})
	public String impersonate(@ModelAttribute("impersonation") ImpersonateForm form, RedirectAttributes redirect) {
		List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
		for(UserRole r : form.getRoles()) {
   	 		gas.add(new SimpleGrantedAuthority("ROLE_"+r.getName()));
		}
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(form.getLogin(), null, gas));
		
		redirect.addFlashAttribute("globalMessage", "Impersonating "+form.getLogin());
		
		// root will do proper redirection based on role 
	    return "redirect:/";
	}
	
	@RequestMapping(value="/impersonate", method=RequestMethod.POST, params={"!save", "cancel"})
	public String impersonateCancel(RedirectAttributes redirect){
		redirect.addFlashAttribute("globalMessage", "Impersonation cancelled");
        return "redirect:/admin/";
	}
}
