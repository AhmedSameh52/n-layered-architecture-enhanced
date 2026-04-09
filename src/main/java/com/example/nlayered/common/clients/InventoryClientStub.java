package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.InventoryCheckRequest;
import com.example.nlayered.common.clients.dto.InventoryCheckResponse;
import com.example.nlayered.common.clients.dto.StockReservationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo stub — always reports stock as available and logs reservation calls.
 * Replace with a real HTTP implementation (RestClient, WebClient, etc.)
 * when a live inventory service is available.
 */
@Slf4j
@Component
public class InventoryClientStub implements InventoryClient {

    @Override
    public InventoryCheckResponse checkAvailability(InventoryCheckRequest request) {
        log.info("STUB checkAvailability — {} line item(s)", request.getItems().size());
        return new InventoryCheckResponse(true, List.of());
    }

    @Override
    public void reserveStock(StockReservationRequest request) {
        log.info("STUB reserveStock — orderId={} items={}", request.getOrderId(), request.getItems().size());
    }

    @Override
    public void releaseStock(StockReservationRequest request) {
        log.info("STUB releaseStock — orderId={}", request.getOrderId());
    }
}