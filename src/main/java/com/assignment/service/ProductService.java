package com.assignment.service;

import java.util.Collections;
import java.util.List;

import com.assignment.dto.PaginatedProductResponse;
import com.assignment.dto.ProductDTO;
import com.assignment.dto.ProductStockResponse;
import com.assignment.entity.Product;
import com.assignment.repository.ProductRepository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductService {

	@Inject
	ProductRepository repository;

	public Uni<Product> create(ProductDTO dto) {
		Product product = new Product();
		product.name = dto.name;
		product.description = dto.description;
		product.price = dto.price;
		product.quantity = dto.quantity;
		return Panache.withTransaction(() -> repository.persist(product));
	}

	public Uni<List<Product>> getAll() {
		return repository.listAll();
	}

	public Uni<Product> getById(Integer id) {
		return repository.findById(id);
	}

	public Uni<Product> update(Integer id, ProductDTO dto) {
		return Panache.withTransaction(() -> repository.findById(id).onItem().ifNotNull().transformToUni(product -> {
			product.name = dto.name;
			product.description = dto.description;
			product.price = dto.price;
			product.quantity = dto.quantity;
			return repository.persist(product);
		}));
	}

	public Uni<Boolean> delete(Integer id) {
		return Panache.withTransaction(() -> repository.deleteById(id));
	}

	public Uni<ProductStockResponse> checkStock(Integer id, Integer count) {
		return repository.findById(id).onItem().ifNotNull()
				.transform(product -> new ProductStockResponse(product.quantity >= count));
	}

	public Uni<List<Product>> getAllSortedByPrice() {
		return repository.listAll(Sort.by("price"));
	}
	
	public Uni<List<Product>> addAll(List<Product> products) {
        return Panache.withTransaction(() ->
            Multi.createFrom().iterable(products)
                .onItem().transformToUniAndMerge(repository::persist)
                .collect().asList()
        );
    }
	
	public Uni<PaginatedProductResponse> getPaginated(int start, int end) {
        return repository.count()
            .onItem().transformToUni(total -> {
                long totalCount = total;
                if (start >= totalCount) {
                    return Uni.createFrom().item(
                        new PaginatedProductResponse(Collections.emptyList(), start + 1, end + 1, totalCount, false)
                    );
                }
                int safeEnd = Math.min(end, Math.toIntExact(totalCount) - 1);
                int safeLimit = Math.max(safeEnd - start + 1, 0);
                boolean hasMore = end < totalCount - 1;

                return repository.findAll()
                    .range(start, safeEnd)
                    .list()
                    .onItem().transform(products ->
                        new PaginatedProductResponse(products, start + 1, safeEnd + 1, totalCount, hasMore)
                    );
            });
    }

	public void deletAllProducts() {
		// TODO Auto-generated method stub
		Panache.withTransaction(() -> repository.deleteAll());
	}

}
