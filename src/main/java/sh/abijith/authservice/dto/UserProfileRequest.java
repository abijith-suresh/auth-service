package sh.abijith.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.abijith.authservice.model.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
