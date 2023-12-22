package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfoRequest {

    @NotNull(message = "Please select Education Term")
    private Long educationTermId;

    @DecimalMax("100.0")
    @DecimalMin("0.0")
    @NotNull(message = "Please enter Midterm Exam")
    private Double midtermExam; // 0 -100

    @DecimalMax("100.0")
    @DecimalMin("0.0")
    @NotNull(message = "Please enter Final Exam")
    private Double finalExam; // 0 -100

    @NotNull(message = "Please enter Absentee")
    private Integer absentee;

    @NotNull(message = "Please enter Info Note")
    private String infoNote;

    @NotNull(message = "Please select Lesson")
    private Long lessonId;

    @NotNull(message = "Please select Student")
    private Long studentId;

}