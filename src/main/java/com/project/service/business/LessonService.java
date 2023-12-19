package com.project.service.business;

import com.project.entity.concretes.business.Lesson;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.LessonMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.LessonRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonResponse;
import com.project.repository.business.LessonRepository;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {

        //!!! lesson Name ile conflict kontrolu
        isLessonExistByLessonName(lessonRequest.getLessonName());
        Lesson lesson = lessonMapper.mapLessonRequestToLesson(lessonRequest);
        //!!! DTO --> POJO
       // Lesson savedLesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));
        Lesson savedLesson = lessonRepository.save(lesson);

        return ResponseMessage.<LessonResponse>builder()
                .message(SuccessMessages.LESSON_SAVE)
                .object(lessonMapper.mapLessonToLessonResponse(savedLesson))
                .status(HttpStatus.CREATED)
                .build();
    }

    private boolean isLessonExistByLessonName(String lessonName){ // JAVA , java
        boolean lessonExist =  lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName);

        if(lessonExist){
            throw new ConflictException(
                    String.format(ErrorMessages.ALREADY_EXIST_LESSON_WITH_LESSON_NAME_MESSAGE,lessonName));
        } else {
            return false;
        }
    }

    public ResponseMessage deleteLessonById(Long id) {

        isLessonExistById(id);
        lessonRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_DELETE)
                .status(HttpStatus.OK)
                .build();
    }

    private Lesson isLessonExistById(Long id){
        return lessonRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_WITH_ID_MESSAGE,id)));
    }
    public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {
        if(lessonRepository.getLessonByLessonName(lessonName).isPresent()) {//null değilse içi doluysa
            return ResponseMessage.<LessonResponse>builder()
                    .message(SuccessMessages.LESSON_FOUND)
                    .object(lessonMapper.mapLessonToLessonResponse(
                            lessonRepository.getLessonByLessonName(lessonName).get()))//optional yapının içerisindeki var olan bilgiyi çekmek için get()
                    .build();
        } else {
            return ResponseMessage.<LessonResponse>builder()//orElseThrow() bestpractice
                    .message(String.format(ErrorMessages.NOT_FOUND_LESSON_WITH_LESSON_NAME, lessonName))
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public Page<LessonResponse> findLessonByPage(int page, int size, String sort, String type) {
       Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return lessonRepository.findAll(pageable).map(lessonMapper::mapLessonToLessonResponse);
    }


    public Set<Lesson> getAllLessonByLessonId(Set<Long> idSet) { // 2;4;6
        return idSet.stream()
                .map(this::isLessonExistById)
                .collect(Collectors.toSet());
    }

    public LessonResponse updateLessonById(Long lessonId, LessonRequest lessonRequest) {
        Lesson lesson = isLessonExistById(lessonId); //db den aldığımız nullsafe

        if(
                !(lesson.getLessonName().equals(lessonRequest.getLessonName())) &&
                        (lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonRequest.getLessonName()))
        ){
            throw new ConflictException(
                    String.format(ErrorMessages.ALREADY_EXIST_LESSON_WITH_LESSON_NAME_MESSAGE, lessonRequest.getLessonName()));
        }

//        lesson.setLessonName(lessonRequest.getLessonName());
//        lesson.setCreditScore(lessonRequest.getCreditScore());
//        lesson.setIsCompulsory(lessonRequest.getIsCompulsory());

        Lesson updatedLesson = lessonMapper.mapLessonRequestToUpdatedLesson(lessonId, lessonRequest);
        updatedLesson.setLessonPrograms(lesson.getLessonPrograms());//request -> pojo burda değişiklik yaptığımızda hepsini
        //set yapmazsak setlenmeyen değerler null olur.

        Lesson savedLesson =  lessonRepository.save(updatedLesson);
        //Lesson savedLesson =  lessonRepository.save(lesson);


        return lessonMapper.mapLessonToLessonResponse(savedLesson);

    }
}


