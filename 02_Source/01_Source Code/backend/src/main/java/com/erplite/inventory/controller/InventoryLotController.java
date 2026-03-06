package com.erplite.inventory.controller;

import com.erplite.inventory.dto.InventoryLotRequestDTO;
import com.erplite.inventory.dto.InventoryLotResponseDTO;
import com.erplite.inventory.entity.InventoryLot.LotStatus;
import com.erplite.inventory.entity.InventoryTransaction;
import com.erplite.inventory.service.InventoryLotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryLotController {

    private final InventoryLotService lotService;

    /**
     * GET /api/lots
     * Query params: materialId (optional), status (optional)
     */
    @GetMapping
    public ResponseEntity<List<InventoryLotResponseDTO>> getLots(
            @RequestParam(required = false) String materialId,
            @RequestParam(required = false) LotStatus status) {
        return ResponseEntity.ok(lotService.getLots(materialId, status));
    }

    /**
     * GET /api/lots/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryLotResponseDTO> getLotById(@PathVariable String id) {
        return ResponseEntity.ok(lotService.getLotById(id));
    }

    /**
     * POST /api/lots/receive
     * Tạo lot mới khi nhận hàng.
     */
    @PostMapping("/receive")
    public ResponseEntity<InventoryLotResponseDTO> receiveNewLot(
            @Valid @RequestBody InventoryLotRequestDTO dto) {
        InventoryLotResponseDTO created = lotService.receiveNewLot(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PATCH /api/lots/{id}/status
     * Body: { "status": "Accepted", "performedBy": "jdoe" }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<InventoryLotResponseDTO> updateLotStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        LotStatus newStatus = LotStatus.valueOf(body.getOrDefault("status", ""));
        String performedBy = body.get("performedBy");
        return ResponseEntity.ok(lotService.updateLotStatus(id, newStatus, performedBy));
    }

    /**
     * GET /api/lots/{id}/transactions
     * Lịch sử giao dịch của lot.
     */
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<InventoryTransaction>> getLotTransactions(@PathVariable String id) {
        return ResponseEntity.ok(lotService.getLotTransactions(id));
    }
}
