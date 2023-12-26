package com.project.controller.business;

import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid
                                                                    LessonProgramRequest lessonProgramRequest){
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }

    @GetMapping("/getAll")// http://localhost:8080/lessonPrograms/getAll + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllLessonPrograms(){
        return lessonProgramService.getAllLessonPrograms();
    }

    @GetMapping("/getById/{id}")// http://localhost:8080/lessonPrograms/getById/1  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getLessonProgramById(@PathVariable Long id){
        return lessonProgramService.getLessonProgramById(id);
    }

    // herhangi bir kullanici atamasi yapilmayan lessonprogramlari getiren metod
    @GetMapping("/getAllUnassigned")// http://localhost:8080/lessonPrograms/getAllUnassigned + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllUnassigned();
    }

    @GetMapping("getAllAssigned")// http://localhost:8080/lessonPrograms/getAllAssigned + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned(){
        return lessonProgramService.getAllAssigned();
    }

    @DeleteMapping("/delete/{id}")// http://localhost:8080/lessonPrograms/delete/1 + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteLessonProgramById(@PathVariable Long id){
        return lessonProgramService.deleteLessonProgramById(id);
    }

    @GetMapping("/getAllLessonProgramWithPage") // http://localhost:8080/lessonPrograms/getAllLessonProgramWithPage?page=0&size=10&sort=id&type=desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> getAllLessonProgramWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return   lessonProgramService.getAllLessonProgramWithPage(page,size,sort,type);
    }

    //Bir ogretmenin kendine ait lessonProgramlari getiriyoruz
    @GetMapping("/getAllLessonProgramByTeacher")// http://localhost:8080/lessonPrograms/getAllLessonProgramByTeacher  + GET
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacher(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    //Bir Student kendine ait lessonProgramlari getiriyoruz
    @GetMapping("/getAllLessonProgramByStudent")// http://localhost:8080/lessonPrograms/getAllLessonProgramByStudent  + GET
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    // Not: ( ODEV ) getLessonProgramsByTeacherId() ******

    @GetMapping("/getAllLessonProgramByTeacherId/{teacherId}")//http://localhost:8080/lessonPrograms/getByTeacherId/1 +GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<LessonProgramResponse> getByTeacherId(@PathVariable Long teacherId){
        return lessonProgramService.getByTeacherId(teacherId);
    }

    // Not : ( ODEV ) getLessonProgramsByStudentId() *****
    @GetMapping("/getAllLessonProgramByStudentId/{studentId}")//http://localhost:8080/lessonPrograms/getByStudentId/1 +GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<LessonProgramResponse> getByStudentId(@PathVariable Long studentId){
        return lessonProgramService.getByStudentId(studentId);
    }

}