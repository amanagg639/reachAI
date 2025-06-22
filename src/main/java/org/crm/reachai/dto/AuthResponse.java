package org.crm.reachai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crm.reachai.enums.RoleName;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String userName;
    private String email;
    private RoleName role;
}
