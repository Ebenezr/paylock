package com.blind.paylock.datalayer.dto.request;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateUserRequestDto {

    private String name;
    private String email;
}
