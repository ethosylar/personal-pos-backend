package site.ethosylar.pos.role.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRoleModulesRequest {

    @NotNull
    private List<@Positive Long> moduleIds;
}