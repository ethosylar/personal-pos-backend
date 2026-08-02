package site.ethosylar.pos.category.response;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Integer displayOrder;

    private Boolean active;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}