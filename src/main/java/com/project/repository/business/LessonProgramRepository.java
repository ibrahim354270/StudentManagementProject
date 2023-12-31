package com.project.repository.business;

import com.project.entity.concretes.business.LessonProgram;
import com.project.payload.response.business.LessonProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
    List<LessonProgram> findByUsers_IdNull();

    List<LessonProgram> findByUsers_IdNotNull();

    // @@Query("SELECT l From LessonProgram l Where l.users.username=?1")
    // iki tablonun kesişimi olan datalar için
    @Query("SELECT l FROM LessonProgram l INNER JOIN l.users users WHERE users.username = ?1")
    Set<LessonProgram> getLessonProgramByUsersUsername(String username);

    // SQL --> SELECT * FROM lesson_program WHERE lesson_program.id IN (2,3)
    @Query("SELECT l FROM LessonProgram l WHERE l.id IN :lessonIdSet")
    Set<LessonProgram> getLessonProgramByLessonProgramIdList(Set<Long> lessonIdSet);

    Set<LessonProgram> findByUsers_IdEquals(Long userId);
    //bizim yazdığımız query ile aynı işelmi türetilmiş method da yapıyor.

    //  @Query("SELECT l FROM LessonProgram l WHERE l.users.id=?1")
    // Set<LessonProgram> getLessonProgramByUserId(Long teacherId);
    // yukarıdaki kodumuz da hata aldık aşağıdaki gibi innier join ile çalıştı
    // @Query("SELECT l FROM LessonProgram l INNER JOIN l.users users WHERE users.id=?1")
}