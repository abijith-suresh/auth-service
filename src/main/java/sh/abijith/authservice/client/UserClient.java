package sh.abijith.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sh.abijith.authservice.dto.UserProfileRequest;

@FeignClient(name = "user-service", url = "${services.user.base-url}")
public interface UserClient {

    @PostMapping("/users")
    void createUserProfile(@RequestBody UserProfileRequest request);
}