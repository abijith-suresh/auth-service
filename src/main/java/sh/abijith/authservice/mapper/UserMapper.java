package sh.abijith.authservice.mapper;

import org.springframework.stereotype.Component;
import sh.abijith.authservice.dto.RegisterRequest;
import sh.abijith.authservice.dto.UserProfileRequest;
import sh.abijith.authservice.model.User;

@Component
public class UserMapper {

    // Maps a User entity to UserProfileRequest DTO
    public UserProfileRequest toUserProfileRequest(User user, RegisterRequest request) {
        return new UserProfileRequest(
                user.getId(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getRoles()
        );
    }

}
