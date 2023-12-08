package com.project.payload.request.user;

import com.project.payload.request.abstracts.BaseUserRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@Getter
@Setter
@NoArgsConstructor
public class UserRequest extends BaseUserRequest {
}
