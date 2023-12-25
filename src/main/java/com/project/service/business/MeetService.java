package com.project.service.business;

import com.project.entity.concretes.business.Meet;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
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
            meets = meetRepository.getByAdvisorTeacher_IdEquals(userId);
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

    public List<MeetResponse> getAllMeets() {
        return meetRepository.findAll()
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());
    }

    public MeetResponse getAllMeetById(Long id) {
        return meetMapper.mapMeetToMeetResponse(isMeetExist(id));
    }
    private Meet isMeetExist(Long id){
        return meetRepository.findById(id).orElseThrow(()->
        new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE)));
    }

    public ResponseMessage deleteMeetById(Long id) {
        isMeetExist(id);
        meetRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE)
                .status(HttpStatus.OK)
                .build();

    }

    public Page<MeetResponse> getAllMeetWithPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
    }    }
