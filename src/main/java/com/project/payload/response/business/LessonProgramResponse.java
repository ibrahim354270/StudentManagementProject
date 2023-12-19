package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.entity.enums.Day;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonProgramResponse {

    private Long lessonProgramId;
    private Day day;
    private LocalTime startTime;
    private LocalTime stopTime;
    private Set<Lesson> lessonName;
    private EducationTerm educationTerm;
    private Set<TeacherResponse> teachers;
    private Set<StudentResponse> students;

}