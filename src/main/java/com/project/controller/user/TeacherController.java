package com.project.controller.user;
import com.project.payload.request.business.ChooseLessonTeacherRequest;
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
    @GetMapping("/getAllStudentByAdvisorUsername")//localhost:8080/teacher/getAllStudentByAdvisorUsername
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllStudentByAdvisorUsername(HttpServletRequest request){
        String userName = request.getHeader("username");//getAttribute ile aynı işlem-casting e gerek kalmıyor
        return teacherService.getAllStudentByAdvisorUsername(userName);
    }

//Servide-set işlemi yapmadan önce DB'den verileri çekersek patch gibi çalışır.
//ama DB'den userı çekmeden DTO-POJO dönüşümü yaparsak (set işlemi yaparsak),boş bıraktığımız yerler null olur.
//Aslında Patch ile Put arasında fark yoktur.Kod okunabilirliği için yapıyruz.
// Kodu yazan Kişi hangi verileri güncelleyeceğini Bilebilmesi için
//Biz Update işlmelerinde İlk Userı çektik.Riske Atmamak için
    @PatchMapping("/saveAdvisorTeacher/{teacherId}") // http://localhost:8080/teacher/saveAdvisorTeacher/1 + PATCH
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<UserResponse> saveAdvisorTeacher(@PathVariable Long teacherId){
        return teacherService.saveAdvisorTeacher(teacherId);
    }
    @DeleteMapping("/deleteAdvisorTeacherById/{id}") // http://localhost:8080/teacher/deleteAdvisorTeacherById/1 + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(@PathVariable Long id){
        return teacherService.deleteAdvisorTeacherById(id);
    }
    @GetMapping("/getAllAdvisorTeacher")// http://localhost:8080/teacher/getAllAdvisorTeacher + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<UserResponse> getAllAdvisorTeacher(){
        return teacherService.getAllAdvisorTeacher();
    }
    //teacher a ders programı ekleme
    @PostMapping("/addLessonProgram")// http://localhost:8080/teacher/addLessonProgram  + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> chooseLesson(@RequestBody @Valid
                                                         ChooseLessonTeacherRequest chooseLessonTeacherRequest){
        return teacherService.addLessonProgram(chooseLessonTeacherRequest);
    }
}
