package org.memoryextension.java.samples.examMaker.utils.forms;

public class QuestionForm {
	
	private Long examId;
	private String examTitle;
	private String question;

	public QuestionForm() {}
	public QuestionForm(Long examId,String examTitle) {
		this.examId = examId;
		this.examTitle=examTitle;
	}
	public Long getExamId() {
		return examId;
	}
	public void setExamId(Long examId) {
		this.examId = examId;
	}
	public String getExamTitle() {
		return examTitle;
	}
	public void setExamTitle(String examTitle) {
		this.examTitle = examTitle;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
	
}
