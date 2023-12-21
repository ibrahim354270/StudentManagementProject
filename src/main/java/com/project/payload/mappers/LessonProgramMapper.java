package com.project.payload.mappers;

import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.entity.concretes.business.LessonProgram;
import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.business.LessonProgramResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
public class LessonProgramMapper {
    //LessonProgramRequest içinde lesson, educationterm bilgileri Id şeklinde var
    // ama POJO dönüşümü için bize tüm entity lesson educationterm gerekiyor.
    //servis katmanında zaten biz ID ile lesson ,educationterm getirmiştik bunları paramatre içine koyuyoruz.

    //!!! DTO --> POJO
    public LessonProgram mapLessonProgramRequestToLessonProgram(LessonProgramRequest lessonProgramRequest,
                                                                Set<Lesson> lessonSet,
                                                                EducationTerm educationTerm){
        return LessonProgram.builder()
                .startTime(lessonProgramRequest.getStartTime())
                .stopTime(lessonProgramRequest.getStopTime())
                .day(lessonProgramRequest.getDay())
                .lessons(lessonSet)
                .educationTerm(educationTerm)
                .build();
    }
    public LessonProgramResponse mapLessonProgramToLessonProgramResponse(LessonProgram lessonProgram) {

        return LessonProgramResponse.builder()
                .lessonProgramId(lessonProgram.getId())
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonName(lessonProgram.getLessons())
               // .educationTerm(lessonProgram.getEducationTerm())
                .build();

    }
}