package com.project.payload.request.user;

import com.project.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class StudentRequest extends BaseUserRequest {

    @NotNull(message = "Please enter Mother Name")
    @Size(min = 2,max = 16, message = "Your mother name should be at least 2 chars")
    @Pattern(regexp="\\A(?!\\s*\\Z).+", message = "Your mother name must be consist of the characters a-z")
    private String motherName;

    @NotNull(message = "Please enter Father Name")
    @Size(min = 2,max = 16, message = "Your father name should be at least 2 chars")
    @Pattern(regexp="\\A(?!\\s*\\Z).+", message = "Your father name must be consist of the characters a-z")
    private String fatherName;

    @NotNull(message = "Please select Advisor Teacher")
    private Long advisorTeacherId;
}