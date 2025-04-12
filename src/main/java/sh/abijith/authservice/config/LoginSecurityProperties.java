package sh.abijith.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "auth.login")
@Data
@Component
public class LoginSecurityProperties {
    private int maxFailedAttempts = 5;
    private int lockDurationMinutes = 15;
}
