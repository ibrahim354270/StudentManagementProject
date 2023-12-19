package com.project.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgramRequest {

    @NotNull(message = "Please enter day")
    private Day day;

    @NotNull(message = "Please enter start time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm",timezone = "US")
    private LocalTime startTime;

    @NotNull(message = "Please enter stop time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm",timezone = "US")
    private LocalTime stopTime;

    @NotNull(message = "Please select lesson")
    @Size(min = 1, message = "Lesson must not be empty")
    private Set<Long> lessonIdList;

    @NotNull(message = "Please select education term")
    private Long educationTermId;

}