package com.project.controller.business;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.EducationTermResponse;
import com.project.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
@RestController
@RequestMapping("/educationTerms")
@RequiredArgsConstructor
public class EducationTermController {
    private final EducationTermService educationTermService;
    @PostMapping("/save") //http://localhost:8080/educationTerms/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> saveEducationTerm(@RequestBody @Valid EducationTermRequest educationTermRequest){
        return educationTermService.saveEducationTerm(educationTermRequest);
    }
}