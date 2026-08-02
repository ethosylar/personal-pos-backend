package site.ethosylar.pos.category.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank
    @Size(max = 50)
    @Pattern(
        regexp = "^[A-Z0-9_-]+$",
        message = "Code may only contain uppercase letters, numbers, underscores and hyphens."
    )
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @Min(0)
    private Integer displayOrder = 0;
}