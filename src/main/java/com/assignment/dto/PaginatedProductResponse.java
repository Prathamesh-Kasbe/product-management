package com.assignment.dto;

import java.util.List;

import com.assignment.entity.Product;

public class PaginatedProductResponse {
    public List<Product> products;
    public int start;
    public int end;
    public long totalCount;
    public boolean hasMore;

    public PaginatedProductResponse(List<Product> products, int start, int end, long totalCount, boolean hasMore) {
        this.products = products;
        this.start = start;
        this.end = end;
        this.totalCount = totalCount;
        this.hasMore = hasMore;
    }
}
