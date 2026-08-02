package site.ethosylar.pos.common.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterDataFilterRequest {

    private String search;

    private Boolean active;

    @Min(1)
    private Integer page = 1;

    @Min(1)
    @Max(100)
    private Integer perPage = 20;
}