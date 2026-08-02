package site.ethosylar.pos.role.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequest {

    @NotBlank
    @Size(max = 50)
    @Pattern(
        regexp = "^[A-Z][A-Z0-9_]*$",
        message = "Role code must contain only uppercase letters, numbers and underscores."
    )
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;
}