package com.project.contactMessage.service;

import com.project.contactMessage.dto.ContactMessageRequest;
import com.project.contactMessage.dto.ContactMessageResponse;
import com.project.contactMessage.entity.ContactMessage;
import com.project.contactMessage.mapper.ContactMessageMapper;
import com.project.contactMessage.messages.Messages;
import com.project.contactMessage.repository.ContactMessageRepository;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        // DTO--> POJO
        ContactMessage contactMessage = contactMessageMapper.requestToContactMessage(contactMessageRequest) ;
        // Repoya gonderiliyor
        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder()
                .message("ContactMessage Created Successfully")
                .status(HttpStatus.CREATED) // 201
                .object(contactMessageMapper.contactMessageToResponse(savedData))
                .build();
    }

    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findAll(pageable).map(contactMessageMapper::contactMessageToResponse);
    }

    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findByEmailEquals(email, pageable).map(contactMessageMapper::contactMessageToResponse);
        //return contactMessageRepository.findByEmailEquals(email, pageable).map(e->contactMessageMapper.contactMessageToResponse(e));
    }

    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(contactMessageMapper::contactMessageToResponse);
    }

    public String deleteById(Long contactMessageId) {
        getContactMessageById(contactMessageId);
        contactMessageRepository.deleteById(contactMessageId);
        return Messages.CONTACT_MESSAGE_DELETED_SUCCESSFULLY;
    }

    public ContactMessage getContactMessageById(Long id){
        return contactMessageRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(Messages.NOT_FOUND_MESSAGE)
        );
    }

    public List<ContactMessage> searchByDateBetween(String beginDateString, String endDateString) {
        try {
            LocalDate beginDate = LocalDate.parse(beginDateString); //bu işlem ile datetime dan sadece tarih kısmını alıyoruz
            LocalDate endDate = LocalDate.parse(endDateString);
            return contactMessageRepository.findMessagesBetweenDates(beginDate,endDate);
        } catch (DateTimeParseException e) {
            throw new ConflictException(Messages.WRONG_DATE_FORMAT);
        }
    }

    public List<ContactMessage> searchBetweenTimes(String startHour, String startMinute, String endHour, String endMinute) {

        try {
            int startH =  Integer.parseInt(startHour); //başlangıç saati
            int startM =  Integer.parseInt(startMinute);//başlangıç dakikası
            int endH =  Integer.parseInt(endHour);//bitiş saati
            int endM =  Integer.parseInt(endMinute);//bitiş dakikası

            return contactMessageRepository.findMessagesBetweenTimes(startH,startM,endH,endM);
        } catch (NumberFormatException e) {
            throw new ConflictException(Messages.WRONG_TIME_FORMAT);
        }
    }
}