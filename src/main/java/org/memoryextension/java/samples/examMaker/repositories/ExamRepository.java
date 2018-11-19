package org.memoryextension.java.samples.examMaker.repositories;

import java.util.List;

import org.memoryextension.java.samples.examMaker.pojos.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
	List<Exam> findByTitle(String title);
}
