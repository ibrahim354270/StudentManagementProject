package com.project.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.entity.concretes.business.LessonProgram;
import com.project.entity.concretes.business.Meet;
import com.project.entity.concretes.business.StudentInfo;
import com.project.entity.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_user")
public class User { //@data-->equals ve hash kod bizim işimizi zorlaştırıyor, performans kaybı

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn;

    private String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Json --> JAVA nesnesine
    private String password; // hassas veri oldugu icin client tarafina gitmesin

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private Boolean built_in; //Değiştirlmesini istemediğimiz değişkenler  için kullanılır

    private String motherName;

    private String fatherName;

    private int studentNumber;

    private boolean isActive;

    private Boolean isAdvisor;

    private Long advisorTeacherId; //  ogrenciler icin gerekli

    @Enumerated(EnumType.STRING)
    private Gender gender; // Bay - bay - BAY

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE)
    private List<StudentInfo> studentInfos;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_lessonprogram",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonProgramList;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "meet_id")
    )
    private List<Meet> meetList;

}