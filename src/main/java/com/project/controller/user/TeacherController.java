package com.project.controller.user;
import com.project.payload.request.user.TeacherRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import com.project.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/save") // http://localhost:8080/teacher/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<TeacherResponse>> saveTeacher(@RequestBody @Valid TeacherRequest teacherRequest){

        return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
    }
    @PutMapping("/update/{userId}")// http://localhost:8080/teacher/update/1 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> updateTeacherForManagers(@RequestBody @Valid TeacherRequest teacherRequest,
                                                                     @PathVariable Long userId){//id =teacher id
        return teacherService.updateTeacherForManagers(teacherRequest,userId);
    }
    //!!! Bir rehber ogretmenin rehberligindeki ogrencilerinin tamamini getiren method
    @GetMapping("/getAllStudentByAdvisorUsername")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllStudentByAdvisorUsername(HttpServletRequest request){
        String userName = request.getHeader("username");//getAttribute ile aynı işlem-casting e gerek kalmıyor
        return teacherService.getAllStudentByAdvisorUsername(userName);
    }
//TODO : addLessonProgramToTeacher

    @PatchMapping("/saveAdvisorTeacher/{teacherId}") // http://localhost:8080/teacher/saveAdvisorTeacher/1 + PATCH
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<UserResponse> saveAdvisorTeacher(@PathVariable Long teacherId){
        return teacherService.saveAdvisorTeacher(teacherId);
    }


}
