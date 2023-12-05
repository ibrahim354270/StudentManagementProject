package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfo {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private Integer absentee;//devamsızlık

   private Double midtermExam;

   private Double finalExam;

   private Double examAverage;

   private String infoNote;

   @Enumerated(EnumType.STRING)
   private Note letterGrade; // AA - aa - Aa -a -A

   @ManyToOne
   @JsonIgnore
   private User teacher;

   @ManyToOne
   @JsonIgnore
   private User student;

   @ManyToOne
   private Lesson lesson;

   @OneToOne
   private EducationTerm educationTerm;


}