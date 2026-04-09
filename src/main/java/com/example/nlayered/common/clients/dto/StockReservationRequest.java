package com.example.nlayered.common.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {
    private Long          orderId;
    private List<LineItem> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private Long productId;
        private int  quantity;
    }
}