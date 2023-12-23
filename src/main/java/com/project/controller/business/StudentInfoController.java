package com.project.controller.business;

import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.request.business.UpdateStudentInfoRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    @PostMapping("/save") // http://localhost:8080/studentInfo/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                @RequestBody @Valid StudentInfoRequest studentInfoRequest){
        return studentInfoService.saveStudentInfo(httpServletRequest,studentInfoRequest);
    }
    @DeleteMapping("/delete/{studentInfoId}")// http://localhost:8080/studentInfo/delete/2 + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage delete(@PathVariable Long studentInfoId){
        return studentInfoService.deleteStudentInfo(studentInfoId);
    }
    @GetMapping("/getAllStudentInfoByPage")// http://localhost:8080/studentInfo/getAllStudentInfoByPage?page=0&size=10&sort=id&type+desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<StudentInfoResponse> getAllStudentInfoByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return studentInfoService.getAllStudentInfoByPage(page,size,sort,type);
    }
    @GetMapping("/get/{studentInfoId}")// http://localhost:8080/studentInfo/studentInfoId + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseEntity<StudentInfoResponse> getStudentInfoById(@PathVariable Long studentInfoId) {
        return ResponseEntity.ok(studentInfoService.getStudentInfoById(studentInfoId));
    }
    @PutMapping("/update/{studentInfoId}")// http://localhost:8080/studentInfo/update/1  + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage<StudentInfoResponse> update(@RequestBody @Valid UpdateStudentInfoRequest studentInfoRequest,
                                                       @PathVariable Long studentInfoId ){
        return studentInfoService.update(studentInfoRequest, studentInfoId);
    }
    @GetMapping("/getAllForTeacher")// http://localhost:8080/studentInfo/getAllForTeacher?page=0&size=1
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return new ResponseEntity<>(studentInfoService.getAllForTeacher(httpServletRequest,page,size), HttpStatus.OK);
    }
    @GetMapping("/getAllForStudent")// http://localhost:8080/studentInfo/getAllForStudent?page=0&size=1
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return new ResponseEntity<>(studentInfoService.getAllForStudent(httpServletRequest,page,size), HttpStatus.OK);
    }
    //aşağıdaki kod ile yaptık normalde service kısmında yapacaktık
//    @GetMapping("/getByStudentId/{studentId}")// http://localhost:8080/studentInfo/getByStudentId/1 + GET
//    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
//    public ResponseEntity<List<StudentInfoResponse>> getStudentInfoByStudentId(@PathVariable Long studentId){
//        return ResponseEntity.ok(studentInfoService.findStudentInfoByStudentId(studentId));
//    }
    @GetMapping("/getByStudentId/{studentId}")// http://localhost:8080/studentInfo/getByStudentId/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseEntity<List<StudentInfoResponse>> getStudentInfoByStudentId(@PathVariable Long studentId){
        List<StudentInfoResponse> studentInfoResponses =studentInfoService.findStudentInfoByStudentId(studentId);
        return ResponseEntity.ok(studentInfoResponses);
    }
}