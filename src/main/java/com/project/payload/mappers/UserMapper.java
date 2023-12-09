package com.project.payload.mappers;

import com.project.entity.concretes.user.User;
import com.project.payload.request.abstracts.BaseUserRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper { //pojoyu DTO ya çevireceğiz

    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDay(user.getBirthDay())
                .birthPlace(user.getBirthPlace())
                .ssn(user.getSsn())
                .email(user.getEmail())
                .userRole(user.getUserRole().getRoleType().name())
                .build();
    }
    //!!! DTO-->POJO

    //DTO-->pojo
//baseUserRequest ini extend eden tüm classlar bunu methodu kullanabilir.
//sadece user request yapsaydık ,teacher ve student request kullanamazlardır,
// oysaki onlarda baseUserRequestten extend yapmıştı.Aynı classın cocukları,abstract kullanmamızın en önemli etkeni
//bu yaptığımız ile polimorrphism özelliği ortaya çıkar.

    //public  User mapUserRequestToUser (UserRequest userRequest){ böyle olmaz
    public User mapUserRequestToUser(BaseUserRequest userRequest) { //Base olursa student/teacher üretebiliriz(poliformizm)

        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .ssn(userRequest.getSsn())
                .birthDay(userRequest.getBirthDay())
                .birthPlace(userRequest.getBirthPlace())
                .phoneNumber(userRequest.getPhoneNumber())
                .gender(userRequest.getGender())
                .email(userRequest.getEmail())
                .built_in(userRequest.getBuiltIn())
                .build();
    }
}
//abstract yerine interface kullansaydikda yine interface date type
// olarak hepsi kullanabilirdik