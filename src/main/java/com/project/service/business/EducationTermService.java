package com.project.service.business;

import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.EducationTermResponse;
import com.project.repository.business.EducationTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {
       validateEducationTermDates(educationTermRequest);
       //TODO: devam edilecek

    }

    //!!! Yrd Methd -1
    private void validateEducationTermDatesForRequest(EducationTermRequest educationTermRequest){
        // registration > start
        if(educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        // end > start
        if(educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }
    }
    //!!! Yrd Method-2
    private void validateEducationTermDates(EducationTermRequest educationTermRequest){

        validateEducationTermDatesForRequest(educationTermRequest);

        //!!! Bir yil icinde bir tane Guz donemi olmali
        if(educationTermRepository.existsByTermAndYear(educationTermRequest.getTerm(),
                educationTermRequest.getStartDate().getYear())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }

        //!!! yil icinde eklenecek educationTerm, DB'deki mevcuttakilerin tarihleri ile cakismamali
        if(educationTermRepository.findByYear(educationTermRequest.getStartDate().getYear())
                .stream()
                .anyMatch(educationTerm->
                                (
                                        educationTerm.getStartDate().equals(educationTermRequest.getStartDate()) // ilk kontrol baslama tarihlerinin ayni olma durumu
                                                || (educationTerm.getStartDate().isBefore(educationTermRequest.getStartDate())//2.kontrol: eklenecek olan ET un startDatei mevcuttakilerin arasina denk geliyor mu
                                                && educationTerm.getEndDate().isAfter(educationTermRequest.getStartDate()))
                                                || (educationTerm.getStartDate().isBefore(educationTermRequest.getEndDate())//3.kontrol : yeni eklenecek ET un EndDate bilgisinin mevcuttakilerin arasinda olma durumu
                                                && educationTerm.getEndDate().isAfter(educationTermRequest.getEndDate()))
                                                || (educationTerm.getStartDate().isAfter(educationTermRequest.getStartDate())//4.kontrol : yeni eklenecek ET . ,mevcuttakileri tamamen kapsiyorsa
                                                && educationTerm.getEndDate().isBefore(educationTermRequest.getEndDate()))

                ))
        ){
            throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
        }

    }

}