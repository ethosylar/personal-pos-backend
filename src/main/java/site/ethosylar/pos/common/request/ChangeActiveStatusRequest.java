package site.ethosylar.pos.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeActiveStatusRequest {

    @NotNull
    private Boolean active;
}