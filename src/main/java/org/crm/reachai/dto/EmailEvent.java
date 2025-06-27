package org.crm.reachai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {
    private String to;
    private String subject;
    private String body;
}
