package site.ethosylar.pos.unit.response;

import lombok.Builder;
import lombok.Getter;
import site.ethosylar.pos.unit.constant.MeasurementType;

@Getter
@Builder
public class UnitOfMeasureResponse {

    private Long id;

    private String code;

    private String name;

    private String symbol;

    private MeasurementType measurementType;

    private Boolean baseUnit;
}