package site.ethosylar.pos.supplier.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplierDetailResponse {

    private Long id;

    private String code;

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private String notes;

    private Boolean active;

    private List<SupplierIngredientResponse> ingredients;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}