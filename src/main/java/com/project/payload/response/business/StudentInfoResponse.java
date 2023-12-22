package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.enums.Note;
import com.project.entity.enums.Term;
import com.project.payload.response.user.StudentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentInfoResponse {

    private Long id;
    private Double midtermExam;
    private Double finalExam;
    private Integer absentee;
    private String infoNote;
    private String lessonName;
    private int creditScore;
    private boolean isCompulsory;
    private Term educationTerm;
    private Double average;
    private Note note;
    private StudentResponse studentResponse;

}