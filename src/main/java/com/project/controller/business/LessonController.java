package com.project.controller.business;

import com.project.entity.concretes.business.Lesson;
import com.project.payload.request.business.LessonRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonResponse;
import com.project.service.business.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/save") // http://localhost:8080/lessons/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonResponse> saveLesson(@RequestBody @Valid LessonRequest lessonRequest){
        return lessonService.saveLesson(lessonRequest);
    }

    @DeleteMapping("/delete/{id}")// http://localhost:8080/lessons/delete/2 + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteLesson(@PathVariable Long id){
        return lessonService.deleteLessonById(id);
    }

    @GetMapping("/getLessonByName") // http://localhost:8080/lessons/getLessonByName?lessonName=Java + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonResponse> getLessonByLessonName(@RequestParam String lessonName){
        return lessonService.getLessonByLessonName(lessonName);
    }
    @GetMapping("/findLessonByPage")  // http://localhost:8080/lessons/findLessonByPage?page=0&size=10&sort=lessonName&type=desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<LessonResponse> findLessonByPage(//list gibi set gibi
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type)
    {
        return lessonService.findLessonByPage(page,size,sort,type);
    }
    @GetMapping("/getAllLessonByLessonId")// http://localhost:8080/lessons/getAllLessonByLessonId?lessonId=1,2,3 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<Lesson> getAllLessonByLessonId(@RequestParam(name = "lessonId") Set<Long> idSet){
        return lessonService.getAllLessonByLessonId(idSet);
    }
    @PutMapping("/update/{lessonId}")// http://localhost:8080/lessons/update/2  + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseEntity<LessonResponse> updateLessonById(@PathVariable Long lessonId,
                                                           @RequestBody LessonRequest lessonRequest){
        return ResponseEntity.ok(lessonService.updateLessonById(lessonId, lessonRequest));
    }

}