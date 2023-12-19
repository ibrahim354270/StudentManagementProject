package com.project.repository.business;

import com.project.entity.concretes.business.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsLessonByLessonNameEqualsIgnoreCase(String lessonName);


    Optional<Lesson> getLessonByLessonName(String lessonName);//findBy ile aynı // derived method,query
}