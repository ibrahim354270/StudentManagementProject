package com.project.payload.mappers;

import com.project.entity.concretes.business.EducationTerm;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.business.EducationTermResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EducationTermMapper {

    //!!! DTO --> POJO
    public EducationTerm mapEducationTermRequestToEducationTerm(EducationTermRequest educationTermRequest){
        return EducationTerm.builder()
                .term(educationTermRequest.getTerm())
                .startDate(educationTermRequest.getStartDate())
                .endDate(educationTermRequest.getEndDate())
                .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
                .build();
    }
    //!!! POJO --> DTO
    public EducationTermResponse mapEducationTermToEducationTermResponse(EducationTerm educationTerm) {
        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }
    public EducationTerm mapEducationTermRequestToUpdatedEducationTerm(Long id, EducationTermRequest educationTermRequest) {
        return mapEducationTermRequestToEducationTerm(educationTermRequest)
                .toBuilder()//yukarıda ki 1.map deki işlemi clone yaptık.tobuilder sayesinde .sadece üstüne ıd bilgisini ekledik. (aynı bilgileri tekrar yazmadk)
                .id(id)
                .build();
    }

}

