package com.project.controller.business;

import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.EducationTermResponse;
import com.project.payload.response.business.MeetResponse;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.service.business.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/save") //http://localhost:8080/meet/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest,
                                                  @RequestBody @Valid MeetRequest meetRequest){
        return meetService.saveMeet(httpServletRequest, meetRequest);
    }
    //Not: getALl
    @GetMapping("/getAll") //http://localhost:8080/meet/getAll + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<MeetResponse> getAllMeetResponse(){
        return meetService.getAllMeets();
    }
// Not: getByMeetId
@GetMapping("/{id}") //http://localhost:8080/meet/1 + GET
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
public MeetResponse getMeetById(@PathVariable Long id){

    return meetService.getAllMeetById(id);
}

// Not: Delete
@DeleteMapping("/delete/{id}")//http://localhost:8080/meet/delete/2 + DELETE
@PreAuthorize("hasAnyAuthority('TEACHER')")
public ResponseMessage deleteMeetById(@PathVariable Long id){
    return meetService.deleteMeetById(id);
}

// Not: getAllWithPage
@GetMapping("/getAllMeetWithPage")// http://localhost:8080/meet//getAllMeetWithPage?page=0&size=10&sort=id&type+desc + GET
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER', 'TEACHER')")
public Page<MeetResponse> ggetAllMeetWithPage(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size,
        @RequestParam(value = "sort") String sort,
        @RequestParam(value = "type") String type
){
    return meetService.getAllMeetWithPage(page,size,sort,type);
}
}