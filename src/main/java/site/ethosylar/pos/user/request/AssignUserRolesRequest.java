package site.ethosylar.pos.user.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignUserRolesRequest {

    @NotNull
    private List<@Positive Long> roleIds;
}