package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetResponse {

    private Long id;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime stopTime;
    private Long advisorTeacherId;
    private String teacherName;
    private String teacherSsn;
    private String username;
    private List<User> students;
}