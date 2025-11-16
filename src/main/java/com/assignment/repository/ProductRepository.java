package com.assignment.repository;

import java.util.List;

import com.assignment.entity.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Integer> {
	
    public Uni<List<Product>> findAllSortedByPrice() {
        return find("ORDER BY price ASC").list();
    }
}