package com.blind.paylock.utils.apis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {
    private ApiResponseHeader header;
    private T body;
}
