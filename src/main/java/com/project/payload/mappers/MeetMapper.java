package com.project.payload.mappers;

import com.project.entity.concretes.business.Meet;
import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.business.MeetResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MeetMapper {

    public Meet mapMeetRequestToMeet(MeetRequest meetRequest){
        return Meet.builder()
                .date(meetRequest.getDate())
                .startTime(meetRequest.getStartTime())
                .stopTime(meetRequest.getStopTime())
                .description(meetRequest.getDescription())
                .build();
    }

    public MeetResponse mapMeetToMeetResponse(Meet savedMeet) {
        return MeetResponse.builder()
                .id(savedMeet.getId())
                .date(savedMeet.getDate())
                .startTime(savedMeet.getStartTime())
                .stopTime(savedMeet.getStopTime())
                .description(savedMeet.getDescription())
                .advisorTeacherId(savedMeet.getAdvisoryTeacher().getId())
                .teacherSsn(savedMeet.getAdvisoryTeacher().getSsn())
                .teacherName(savedMeet.getAdvisoryTeacher().getName())
                .students(savedMeet.getStudentList())
                .build();
    }
}

