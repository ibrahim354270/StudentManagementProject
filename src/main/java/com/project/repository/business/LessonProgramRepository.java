package com.project.repository.business;

import com.project.entity.concretes.business.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
}