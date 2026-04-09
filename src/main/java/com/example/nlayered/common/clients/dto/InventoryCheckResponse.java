package com.example.nlayered.common.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckResponse {
    private boolean allAvailable;
    private List<UnavailableItem> unavailableItems;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnavailableItem {
        private Long   productId;
        private int    requested;
        private int    available;
    }
}