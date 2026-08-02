package site.ethosylar.pos.supplier.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.response.SupplierResponse;

@Component
public class SupplierMapper {

    public SupplierResponse toResponse(
    		SupplierEntity entity) {

        return SupplierResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .contactPerson(entity.getContactPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .postcode(entity.getPostcode())
                .country(entity.getCountry())
                .registrationNo(entity.getRegistrationNo())
                .taxRegistrationNo(entity.getTaxRegistrationNo())
                .paymentTermsDays(entity.getPaymentTermsDays())
                .notes(entity.getNotes())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}