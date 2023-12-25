package com.project.service.business;

import com.project.entity.concretes.business.Meet;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.MeetMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.MeetResponse;
import com.project.repository.business.MeetRepository;
import com.project.service.UserService;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.user.TeacherService;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MethodHelper methodHelper;
    private final DateTimeValidator dateTimeValidator;
    private final UserService userService;
    private final MeetMapper meetMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest, MeetRequest meetRequest) {

        String username = (String) httpServletRequest.getAttribute("username");
        User advisorTeacher = methodHelper.isUserExistByUsername(username);

        methodHelper.checkAdvisor(advisorTeacher);

        dateTimeValidator.checkTimeWithException(meetRequest.getStartTime(), meetRequest.getStopTime());
        // !!! teacher icin meet conflict kontrolu
        checkMeetConflict(advisorTeacher.getId(), meetRequest.getDate(),
                meetRequest.getStartTime(), meetRequest.getStopTime());

        // !!! Student icin meet conflict
        //Bir teacher var ama Bir çok Studetn var o yüzden foreach ile aldık
        for(Long studentId: meetRequest.getStudentIds()){
            User student = methodHelper.isUserExist(studentId);
            methodHelper.checkRole(student, RoleType.STUDENT);

            checkMeetConflict(studentId, meetRequest.getDate(), meetRequest.getStartTime(), meetRequest.getStopTime());
        }

        //!!! Meete katilacak Studentlar getiriliyor
        List<User> students = userService.getStudentById(meetRequest.getStudentIds());

        Meet meet = meetMapper.mapMeetRequestToMeet(meetRequest);
        meet.setStudentList(students);
        meet.setAdvisoryTeacher(advisorTeacher);
        Meet savedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_SAVE)
                .object(meetMapper.mapMeetToMeetResponse(savedMeet))
                .status(HttpStatus.OK)
                .build();



    }
    //BU METHOD HEM TEACHER HEMDE STUDENT İÇİN ÇALIŞIR
    //Eğer teacher ise if girecek ona göre conflict kontrol edecek.eğer student ise else içine girecek öğrenciye göre işlemler yapılacak
    private void checkMeetConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime stopTime){

        List<Meet> meets ;
        if(Boolean.TRUE.equals(userService.getUserByUserId(userId).getIsAdvisor())){
            meets = meetRepository.getByAdvisoryTeacher_IdEquals(userId);
        } else meets = meetRepository.findByStudentList_IdEquals(userId);

        for (Meet meet : meets){
            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime();

            if(meet.getDate().equals(date) &&
                    (
                            (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                                    (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                                    (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                                    (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))

                    )



            ){
                throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
            }
        }

    }
    public List<MeetResponse> getAll() {
        return meetRepository.findAll()
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<MeetResponse> getMeetById(Long meetId) {
        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_FOUND)
                .status(HttpStatus.OK)
                .object(meetMapper.mapMeetToMeetResponse(isMeetExistById(meetId)))
                .build();
    }

    private Meet isMeetExistById(Long meetId){
        return meetRepository
                .findById(meetId).orElseThrow(
                        ()->new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE,meetId)));
    }

    public ResponseMessage delete(Long meetId, HttpServletRequest httpServletRequest) {
        Meet meet = isMeetExistById(meetId);
        //!!! Teacher ise sadece kendi Meet lerini silebilsin
        String userName = (String) httpServletRequest.getAttribute("username");
        User teacher = methodHelper.isUserExistByUsername(userName);
        if(teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)){
            isTeacherControl(meet, teacher);
        }

        meetRepository.deleteById(meetId);
        //TODO : ogrencilerden meet silinecek
        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE)
                .status(HttpStatus.OK)
                .build();
    }
    private void isTeacherControl(Meet meet, User teacher){
        //!!! Teacher ise sadece kendi Meet lerini silebilsin
        if(
                (teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)) && // metodu tetikleyenin Role bilgisi TEACHER ise
                        !(meet.getAdvisoryTeacher().getId().equals(teacher.getId())) // Teacher, baskasinin Meet ini silmeye calisiyorsa
        )
        {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    public Page<MeetResponse> getAllMeetByPage(int page, int size) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size);
        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
    }



//    public List<MeetResponse> getAllMeets() {
//        return meetRepository.findAll()
//                .stream()
//                .map(meetMapper::mapMeetToMeetResponse)
//                .collect(Collectors.toList());
//    }
//
//    public MeetResponse getAllMeetById(Long id) {
//        return meetMapper.mapMeetToMeetResponse(isMeetExist(id));
//    }
//    private Meet isMeetExist(Long id){
//        return meetRepository.findById(id).orElseThrow(()->
//        new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE)));
//    }
//
//    public ResponseMessage deleteMeetById(Long id) {
//        isMeetExist(id);
//        meetRepository.deleteById(id);
//
//        return ResponseMessage.builder()
//                .message(SuccessMessages.MEET_DELETE)
//                .status(HttpStatus.OK)
//                .build();
//
//    }
//
//    public Page<MeetResponse> getAllMeetWithPage(int page, int size, String sort, String type) {
//        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
//        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
//    }
public ResponseMessage<MeetResponse> updateMeet(MeetRequest updateMeetRequest, Long meetId,
                                                HttpServletRequest httpServletRequest) {
    Meet meet = isMeetExistById(meetId);
    String userName = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.isUserExistByUsername(userName);
    //!!! Teacher kendine ait olan meet i mi guncelliyor
    isTeacherControl(meet, teacher);
    //!!! cakisma var mi kontrolu
    dateTimeValidator.checkTimeWithException(updateMeetRequest.getStartTime(), updateMeetRequest.getStopTime());
    //!!! Teacher ve student icin cakisma kontrolu :
    if(!(
            meet.getDate().equals(updateMeetRequest.getDate()) &&
                    meet.getStartTime().equals(updateMeetRequest.getStartTime()) &&
                    meet.getStopTime().equals(updateMeetRequest.getStopTime())
    ))
    {
        //!!! Student icin cakisma kontrolu :
        for (Long studentId : updateMeetRequest.getStudentIds()){
            checkMeetConflict(studentId, updateMeetRequest.getDate(),
                    updateMeetRequest.getStartTime(),updateMeetRequest.getStopTime());
        }

        //!!! Teacher icin cakisma kontrolu
        checkMeetConflict(meet.getAdvisoryTeacher().getId(),
                updateMeetRequest.getDate(),
                updateMeetRequest.getStartTime(),
                updateMeetRequest.getStopTime());
    }

    List<User> students = userService.getStudentById(updateMeetRequest.getStudentIds());
    //!!! DTO --> POJO
    Meet updatedMeet =  meetMapper.mapMeetUpdateRequestToMeet(updateMeetRequest, meetId);
    updatedMeet.setAdvisoryTeacher(meet.getAdvisoryTeacher());
    updatedMeet.setStudentList(students);

    Meet savedMeet = meetRepository.save(updatedMeet);

    return ResponseMessage.<MeetResponse>builder()
            .message(SuccessMessages.MEET_UPDATE)
            .status(HttpStatus.OK)
            .object(meetMapper.mapMeetToMeetResponse(savedMeet))
            .build();

}

    public ResponseEntity<List<MeetResponse>> getAllMeetByTeacher(HttpServletRequest httpServletRequest) {
        String userName = (String) httpServletRequest.getAttribute("username");
        User advisoryTeacher = methodHelper.isUserExistByUsername(userName);
        methodHelper.checkAdvisor(advisoryTeacher);

        List<MeetResponse> meetResponseList =
                meetRepository.getByAdvisoryTeacher_IdEquals(advisoryTeacher.getId())
                        .stream()
                        .map(meetMapper::mapMeetToMeetResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(meetResponseList);

    }

    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest) {
        String userName = (String) httpServletRequest.getAttribute("username");
        User student = methodHelper.isUserExistByUsername(userName);

        methodHelper.checkRole(student, RoleType.STUDENT);

        return  meetRepository.findByStudentList_IdEquals(student.getId())
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());

    }
}
