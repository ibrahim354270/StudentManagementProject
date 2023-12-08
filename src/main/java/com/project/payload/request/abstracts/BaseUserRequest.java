package com.project.payload.request.abstracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuperBuilder

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseUserRequest extends AbstractUserRequest {

    @NotNull(message = "Please enter your password")
    @Size(min=6, max = 60, message = "Your password should be at least 6 chars or maximum 60 chars")
    private String password;

    private Boolean builtIn = false;
}















