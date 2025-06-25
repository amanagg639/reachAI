package org.crm.reachai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiErrorResponseDto {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;



}
