package com.email;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserDTO {
    private String id;
    private String password;
    private String email;
    private String pwReToken;
    private LocalDateTime pwReTokenExpire;
}
