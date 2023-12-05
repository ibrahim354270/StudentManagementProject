package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Day;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;

    @ManyToMany
    @JoinTable(
            name = "lesson_program_lesson",
            joinColumns = @JoinColumn(name = "lessonprogram_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<Lesson> lessons;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private EducationTerm educationTerm;

    @ManyToMany(mappedBy ="lessonProgramList", fetch = FetchType.EAGER)
    private Set<User> users;

    @PreRemove
    private void removeLessonProgramFromUser(){
        users.forEach(user -> user.getLessonProgramList().remove(this));
    }
}