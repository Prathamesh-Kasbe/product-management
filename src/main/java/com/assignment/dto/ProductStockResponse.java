package com.assignment.dto;

import com.assignment.entity.Product;

public class ProductStockResponse {
    public boolean available;

    public ProductStockResponse(boolean available) {
        this.available = available;
    }
}

