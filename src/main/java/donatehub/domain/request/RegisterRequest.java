package donatehub.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Channel URL is required")
    @Length(min = 6, max = 20, message = "Channel URL must be between 6 and 20 characters")
    private String channelUrl;

    @NotEmpty(message = "Channel name is required")
    @Length(min = 2, max = 20, message = "Channel name must be between 2 and 20 characters")
    private String channelName;

    @NotEmpty(message = "Description is required")
    @Length(min = 10, max = 100, message = "Description must be between 10 and 100 characters")
    private String description;
}
