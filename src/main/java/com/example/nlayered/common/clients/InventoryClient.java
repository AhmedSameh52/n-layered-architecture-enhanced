package com.example.nlayered.common.clients;

import com.example.nlayered.common.clients.dto.InventoryCheckRequest;
import com.example.nlayered.common.clients.dto.InventoryCheckResponse;
import com.example.nlayered.common.clients.dto.StockReservationRequest;

public interface InventoryClient {

    InventoryCheckResponse checkAvailability(InventoryCheckRequest request);

    void reserveStock(StockReservationRequest request);

    void releaseStock(StockReservationRequest request);
}