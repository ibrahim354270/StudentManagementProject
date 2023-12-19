package com.project.service.business;

import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.repository.business.LessonProgramRepository;

import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final DateTimeValidator dateTimeValidator;

    public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
        Set<Lesson> lessons =  lessonService.getAllLessonByLessonId(lessonProgramRequest.getLessonIdList());
        EducationTerm educationTerm = educationTermService.findEducationTermById(
                lessonProgramRequest.getEducationTermId());

        //!!! yukarda gelen lessons ici bos ise
        if(lessons.isEmpty()){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_IN_LIST);
        }

        //!!!  zaman kontrolu
        dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(),
                lessonProgramRequest.getStopTime());

        //!!! DTO --> POJO

    }
}