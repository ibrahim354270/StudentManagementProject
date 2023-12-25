package com.project.repository.business;

import com.project.entity.concretes.business.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    List<Meet> getByAdvisoryTeacher_IdEquals(Long advisorTeacherId);

    List<Meet> findByStudentList_IdEquals(Long studentId);
}
