package org.crm.reachai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.crm.reachai.enums.RoleName;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String userName;
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private RoleName role;
}
