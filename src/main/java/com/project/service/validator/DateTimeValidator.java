package com.project.service.validator;

import com.project.entity.concretes.business.LessonProgram;
import com.project.exception.BadRequestException;
import com.project.payload.messages.ErrorMessages;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DateTimeValidator {

    public boolean checkTime(LocalTime start, LocalTime stop){

        return start.isAfter(stop) || start.equals(stop);
    }

    public void checkTimeWithException(LocalTime start, LocalTime stop){
        if(checkTime(start, stop)){
            throw new BadRequestException(ErrorMessages.TIME_NOT_VALID_MESSAGE);
        }
    }

    private void checkDuplicateLessonPrograms(Set<LessonProgram> lessonPrograms){

        Set<String> uniqueLessonProgramDays = new HashSet<>();
        Set<LocalTime> existingLessonProgramStartTime = new HashSet<>();
        Set<LocalTime> existingLessonProgramStopTime = new HashSet<>();

        for (LessonProgram lessonProgram :lessonPrograms ){
            String lessonProgramDay = lessonProgram.getDay().name();

            //!!! Karsilastirilan LessonProgramlar ayni gunde mi ??
            if(uniqueLessonProgramDays.contains(lessonProgramDay)){
                //!!! Baslama saatine gore kontrol
                for (LocalTime startTime : existingLessonProgramStartTime){
                    //!!! Baslama saati esit ise
                    if(lessonProgram.getStartTime().equals(startTime)){
                        throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
                    }
                    //!!! baslama saati, arasinda olma durumu
                    if(lessonProgram.getStartTime().isBefore(startTime) && lessonProgram.getStopTime().isAfter(startTime)){
                        throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
                    }
                }
                //!!! Bitis saatine gore kontrol
                for(LocalTime stopTime : existingLessonProgramStopTime){
                    if(lessonProgram.getStartTime().isBefore(stopTime) && lessonProgram.getStopTime().isAfter(stopTime)){
                        throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
                    }
                }
            }

            // yukardaki kontrollerden gecen LP nin degiskenlerini set yapilara atiyorum
            uniqueLessonProgramDays.add(lessonProgramDay);
            existingLessonProgramStartTime.add(lessonProgram.getStartTime());
            existingLessonProgramStopTime.add(lessonProgram.getStopTime());

        }
    }

    private void checkDuplicateLessonPrograms(Set<LessonProgram> existLessonProgram ,
                                              Set<LessonProgram> lessonProgramRequest){
        for (LessonProgram requestLessonProgram : lessonProgramRequest){
            String requestLessonProgramDay = requestLessonProgram.getDay().name();
            LocalTime requestStart = requestLessonProgram.getStartTime();
            LocalTime requestStop = requestLessonProgram.getStopTime();

            if(existLessonProgram.stream()
                    .anyMatch(lessonProgram ->
                            lessonProgram.getDay().name().equals(requestLessonProgramDay)
                                    && (lessonProgram.getStartTime().equals(requestStart) // LP1 (SALI 09:00)  -- LP2 ( SALI 09:00 )
                                    || (lessonProgram.getStartTime().isBefore(requestStart) && lessonProgram.getStopTime().isAfter(requestStart)) // LP1 ( Sali 09:00 - 11:00 ) --  LP2 ( Sali 10:00 - 12:00 )
                                    || (lessonProgram.getStartTime().isBefore(requestStop) && lessonProgram.getStopTime().isAfter(requestStop))
                                    || (lessonProgram.getStartTime().isAfter(requestStart) && lessonProgram.getStopTime().isBefore(requestStop))  ))) {
                throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
            }

        }
    }
    public void checkLessonPrograms(Set<LessonProgram> existLessonProgram,
                                    Set<LessonProgram> lessonProgramRequest){
        if(existLessonProgram.isEmpty() && lessonProgramRequest.size() > 1){
            checkDuplicateLessonPrograms(lessonProgramRequest);
        } else {
            checkDuplicateLessonPrograms(lessonProgramRequest);
            checkDuplicateLessonPrograms(existLessonProgram,lessonProgramRequest);
        }
    }


}
