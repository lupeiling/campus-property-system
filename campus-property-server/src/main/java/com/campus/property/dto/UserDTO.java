package com.campus.property.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private String avatar;
    private String role;
    private String department;
    private String title;
    private String studentNo;
    private Integer status;
}
