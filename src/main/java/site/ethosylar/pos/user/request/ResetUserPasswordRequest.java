package site.ethosylar.pos.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetUserPasswordRequest {

    @NotBlank
    @Size(
        min = 8,
        max = 100,
        message = "Password must contain between 8 and 100 characters."
    )
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}