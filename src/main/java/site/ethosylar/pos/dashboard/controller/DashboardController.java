package site.ethosylar.pos.dashboard.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.dashboard.response.DashboardSummaryResponse;
import site.ethosylar.pos.dashboard.response.InventoryStockAlertResponse;
import site.ethosylar.pos.dashboard.response.InventorySummaryResponse;
import site.ethosylar.pos.dashboard.response.MonthlySalesTrendResponse;
import site.ethosylar.pos.dashboard.response.RecentInventoryMovementResponse;
import site.ethosylar.pos.dashboard.response.TopProductResponse;
import site.ethosylar.pos.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'DASHBOARD', 'VIEW')")
	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryResponse> getSummary(

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return ResponseEntity.ok(dashboardService.getSummary(date));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'DASHBOARD', 'VIEW')")
	@GetMapping("/sales-trend")
	public ResponseEntity<List<MonthlySalesTrendResponse>> getSalesTrend(

			@RequestParam(required = false) Integer year) {

		int selectedYear = year != null ? year : LocalDate.now(ZoneId.of("Asia/Kuala_Lumpur")).getYear();

		return ResponseEntity.ok(dashboardService.getSalesTrend(selectedYear));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'DASHBOARD', 'VIEW')")
	@GetMapping("/top-products")
	public ResponseEntity<List<TopProductResponse>> getTopProducts(

			@RequestParam Integer year,

			@RequestParam Integer month,

			@RequestParam(defaultValue = "10") Integer limit) {

		return ResponseEntity.ok(dashboardService.getTopProducts(year, month, limit));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'DASHBOARD', 'VIEW')")
	@GetMapping("/inventory-summary")
	public ResponseEntity<InventorySummaryResponse> getInventorySummary() {

		return ResponseEntity.ok(dashboardService.getInventorySummary());
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'DASHBOARD', 'VIEW')")
	@GetMapping("/stock-alerts")
	public ResponseEntity<List<InventoryStockAlertResponse>> getStockAlerts() {

		return ResponseEntity.ok(dashboardService.getStockAlerts());
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'DASHBOARD', 'VIEW')")
	@GetMapping("/inventory-stock")
	public ResponseEntity<List<InventoryStockAlertResponse>> getInventoryStock() {

		return ResponseEntity.ok(dashboardService.getInventoryStock());
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'DASHBOARD', 'VIEW')")
	@GetMapping("/recent-stock-movements")
	public ResponseEntity<List<RecentInventoryMovementResponse>> getRecentStockMovements(

			@RequestParam(defaultValue = "10") Integer limit) {

		return ResponseEntity.ok(dashboardService.getRecentInventoryMovements(limit));
	}
}