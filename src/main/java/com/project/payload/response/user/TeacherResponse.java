package com.project.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.LessonProgram;
import com.project.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherResponse extends BaseUserResponse {

    private Set<LessonProgram> lessonPrograms;
    private Boolean isAdvisorTeacher;
}













