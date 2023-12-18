package com.project.payload.response.business;

import com.project.entity.enums.Term;
import com.project.payload.response.abstracts.BaseUserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EducationTermResponse {//education diğerleri ile ortak değişknleri yok o yüzden extend yapmadık.

    private Long id;
    private Term term;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastRegistrationDate;
}