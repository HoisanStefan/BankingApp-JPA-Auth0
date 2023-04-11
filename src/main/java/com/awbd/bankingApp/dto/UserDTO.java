package com.awbd.bankingApp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class UserDTO {
    private String firstName;
    @NotNull
    private String lastName;
    private String phone;
    private String address;
    @NotNull
    private String email;
    @NotNull
    private String id;

    public UserDTO() {}

    public UserDTO(String firstName, String lastName, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }
}
