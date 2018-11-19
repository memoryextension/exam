package org.memoryextension.java.samples.examMaker.pojos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ExamAnswer {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Exam exam;
	
	@ManyToOne
	private AppUser user;

	@ElementCollection
	private List<String> answers;

	public ExamAnswer() {}
	public ExamAnswer(Exam exam,AppUser user) {
		this.exam=exam;
		this.user=user;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
	
	public void sizeAnswers(int size) {
		this.answers = new ArrayList<String>(Collections.nCopies(size, ""));
		
	}
	

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}
	
	
}
