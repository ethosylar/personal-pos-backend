package site.ethosylar.pos.supplier.projection;

public interface SupplierSummaryProjection {

	Long getId();

	String getCode();

	String getName();

	String getContactPerson();

	String getPhone();

	String getEmail();

	Boolean getActive();
}