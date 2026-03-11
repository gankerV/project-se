package com.erplite.inventory.controller;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.report.*;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import com.erplite.inventory.entity.Material.MaterialType;
import com.erplite.inventory.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(reportService.getDashboard());
    }

    @GetMapping("/near-expiry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<NearExpiryItemResponse>> getNearExpiry(
            @RequestParam(defaultValue = "30") int days,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reportService.getNearExpiry(days, pageable));
    }

    @GetMapping("/lots/{lotId}/trace")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LotTraceResponse> getLotTrace(@PathVariable String lotId) {
        return ResponseEntity.ok(reportService.getLotTrace(lotId));
    }

    @GetMapping("/qc")
    @PreAuthorize("hasAnyRole('Admin','InventoryManager','QualityControl')")
    public ResponseEntity<QCReportResponse> getQCReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getQCReport(from, to));
    }

    @GetMapping("/inventory")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InventorySnapshotResponse>> getInventory(
            @RequestParam(required = false) LotStatus status,
            @RequestParam(required = false) MaterialType materialType) {
        return ResponseEntity.ok(reportService.getInventorySnapshot(status, materialType));
    }
}
