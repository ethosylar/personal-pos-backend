package site.ethosylar.pos.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserStatusRequest {

    @NotNull
    @Positive
    private Long userStatusId;
}