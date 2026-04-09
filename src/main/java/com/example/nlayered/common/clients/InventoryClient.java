package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.InventoryCheckRequest;
import com.example.nlayered.common.clients.dto.InventoryCheckResponse;
import com.example.nlayered.common.clients.dto.StockReservationRequest;
import com.example.nlayered.common.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "inventory-service",
        url = "${services.inventory.url}",
        configuration = OpenFeignConfig.class
)
public interface InventoryClient {

    @PostMapping("/api/inventory/check")
    InventoryCheckResponse checkAvailability(@RequestBody InventoryCheckRequest request);

    @PostMapping("/api/inventory/reserve")
    void reserveStock(@RequestBody StockReservationRequest request);

    @PostMapping("/api/inventory/release")
    void releaseStock(@RequestBody StockReservationRequest request);
}