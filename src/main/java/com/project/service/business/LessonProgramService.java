package com.project.service.business;

import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.entity.concretes.business.LessonProgram;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.LessonProgramMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.repository.business.LessonProgramRepository;
import com.project.repository.business.LessonRepository;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final DateTimeValidator dateTimeValidator;
    private final LessonProgramMapper lessonProgramMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
        Set<Lesson> lessons =  lessonService.getAllLessonByLessonId(lessonProgramRequest.getLessonIdList());
        EducationTerm educationTerm = educationTermService.findEducationTermById(
                lessonProgramRequest.getEducationTermId());

        //!!! yukarda gelen lessons ici bos ise
        if(lessons.isEmpty()){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_IN_LIST);
        }

        //!!!  zaman kontrolu
        dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(),
                lessonProgramRequest.getStopTime());

        //!!! DTO --> POJO
        LessonProgram lessonProgram =
                lessonProgramMapper.mapLessonProgramRequestToLessonProgram(lessonProgramRequest,lessons,educationTerm);

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_SAVE)
                .status(HttpStatus.CREATED)
                .object(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
                .build();
    }

    public List<LessonProgramResponse> getAllLessonPrograms() {
        return lessonProgramRepository
                .findAll()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public LessonProgramResponse getLessonProgramById(Long id) {
        return lessonProgramMapper.mapLessonProgramToLessonProgramResponse(isLessonProgramExistById(id));
    }

    private LessonProgram isLessonProgramExistById(Long id){
        return lessonProgramRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE,id)));
    }

    public List<LessonProgramResponse> getAllUnassigned() {
        return lessonProgramRepository.findByUsers_IdNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public List<LessonProgramResponse> getAllAssigned() {

        return lessonProgramRepository.findByUsers_IdNotNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }
    public ResponseMessage deleteLessonProgramById(Long id) {
        isLessonProgramExistById(id);
        lessonProgramRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_PROGRAM_DELETE)
                .status(HttpStatus.OK)
                .build();
    }
    public Page<LessonProgramResponse> getAllLessonProgramWithPage(int page, int size, String sort, String type) {
        Pageable pageable =  pageableHelper.getPageableWithProperties(page, size, sort, type);
        return lessonProgramRepository.findAll(pageable)
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse);
    }
    public Set<LessonProgramResponse> getAllLessonProgramByUser(HttpServletRequest httpServletRequest) {
        String userName = (String) httpServletRequest.getAttribute("username");

        return lessonProgramRepository.getLessonProgramByUsersUsername(userName)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());
    }
    //!!! TeacherService icin yazildi
    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdSet){
        Set<LessonProgram> lessonPrograms = lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdSet);

        if(lessonPrograms.isEmpty()){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE_WITHOUT_ID_LIST);
        }

        return lessonPrograms;
    }

}
