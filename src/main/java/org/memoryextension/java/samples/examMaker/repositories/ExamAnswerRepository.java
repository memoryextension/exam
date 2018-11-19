package org.memoryextension.java.samples.examMaker.repositories;



import java.util.List;
import java.util.Optional;

import org.memoryextension.java.samples.examMaker.pojos.AppUser;
import org.memoryextension.java.samples.examMaker.pojos.Exam;
import org.memoryextension.java.samples.examMaker.pojos.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer,Long>{
	List<ExamAnswer> findByExam(Exam exam);
	List<ExamAnswer> findByUser(AppUser user);
	Optional<ExamAnswer> findByExamAndUser(Exam exam, AppUser user);
}
