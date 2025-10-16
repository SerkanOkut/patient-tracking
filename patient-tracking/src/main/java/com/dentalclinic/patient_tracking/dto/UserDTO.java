package com.dentalclinic.patient_tracking.dto;


import com.dentalclinic.patient_tracking.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String tcNumber;
    private Role role;

}
