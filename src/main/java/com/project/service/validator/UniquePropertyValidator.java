package com.project.service.validator;

import com.project.entity.concretes.user.User;
import com.project.exception.ConflictException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.abstracts.AbstractUserRequest;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataException;
import org.springframework.stereotype.Component;

@Component //@Component class --- @Bean method seviyesi
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final UserRepository userRepository;

    public void checkDuplicate(String username, String ssn, String phone, String email){

        if (userRepository.existsByUsername(username)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME, username));

            }
        if (userRepository.existsBySsn(ssn)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN, ssn));

        }
        if (userRepository.existsByPhoneNumber(phone)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE, phone));

        }
        if (userRepository.existsByEmail(email)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL, email));

        }

        }
        //db deki user ile güncellenmek istenen user
    public void checkUniqueProperties(User user, AbstractUserRequest abstractUserRequest){

        String updatedUsername = "";
        String updatedSsn = "";
        String updatedPhone = "";
        String updatedEmail = "";
        boolean isChanged = false; // update ile gelen veriler user ile aynı ise

        // !!! username degisti mi ?? eşit değilse
        if(!user.getUsername().equalsIgnoreCase(abstractUserRequest.getUsername())){
            updatedUsername = abstractUserRequest.getUsername();
            isChanged = true;
        }

        // !!! Ssn degisti mi ?? aynı ise okumuyor if kısmını es geçiyor
        if(!user.getSsn().equalsIgnoreCase(abstractUserRequest.getSsn())){
            updatedSsn = abstractUserRequest.getSsn();
            isChanged = true;
        }

        // !!! Phone degisti mi ??
        if(!user.getPhoneNumber().equalsIgnoreCase(abstractUserRequest.getPhoneNumber())){
            updatedPhone = abstractUserRequest.getPhoneNumber();
            isChanged = true;
        }

        // !!! Email degisti mi ??
        if(!user.getEmail().equalsIgnoreCase(abstractUserRequest.getEmail())){
            updatedEmail = abstractUserRequest.getEmail();
            isChanged = true;
        }

        if(isChanged){//username-Ssn-Phone-Email -->herhangi biri true olsa bile bu satır çalışacak
            checkDuplicate(updatedUsername, updatedSsn, updatedPhone, updatedEmail);
        }
    }
    }


