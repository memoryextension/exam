package org.memoryextension.java.samples.examMaker.pojos;




import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Exam {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull(message = "Need a Title")
	private String title;
	private String message;

	@ElementCollection
	private List<String> questions;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

	public void addQuestion(String question) {
		if(questions==null) {
			questions= new ArrayList<String>();
		}
		questions.add(question);
	}
	
}
