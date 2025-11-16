package com.assignment.resource;

import java.util.List;

import com.assignment.dto.ProductDTO;
import com.assignment.entity.Product;
import com.assignment.service.ProductService;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService service;

    @POST
    public Uni<Response> create(ProductDTO dto) {
        return service.create(dto)
            .onItem().transform(product -> Response.status(Status.CREATED).entity(product).build());
    }

    @GET
    public Uni<List<Product>> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getById(@PathParam("id") Integer id) {
        return service.getById(id)
            .onItem().ifNotNull().transform(product -> Response.ok(product).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND)::build);
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> update(@PathParam("id") Integer id, ProductDTO dto) {
        return service.update(id, dto)
            .onItem().ifNotNull().transform(product -> Response.ok(product).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND)::build);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Integer id) {
        return service.delete(id)
            .onItem().transform(deleted -> deleted ? Response.noContent().build() : Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/stock")
    public Uni<Response> checkStock(@PathParam("id") Integer id, @QueryParam("count") Integer count) {
        return service.checkStock(id, count)
            .onItem().transform(resp -> Response.ok(resp).build());
    }

    @GET
    @Path("/sorted")
    public Uni<List<Product>> getSortedByPrice() {
        return service.getAllSortedByPrice();
    }
    
    /*
     * Additional Functionalities implemented below
     * Adding multiple products in a single request
     * Pagination support to get products in a specified range
     */
    
    @POST
    @Path("/bulk")
    public Uni<Response> addMultiple(List<Product> products) {
        return service.addAll(products)
            .onItem().transform(inserted -> Response.status(Status.CREATED).entity(inserted).build());
    }
    
    @GET
    @Path("/range")
    public Uni<Response> getRange(@QueryParam("start") int start, @QueryParam("end") int end) {
        if (start < 0 || end < start) {
            return Uni.createFrom().item(
                Response.status(Response.Status.BAD_REQUEST)
                        .entity(new JsonObject().put("error", "Invalid index range"))
                        .build()
            );
        }
        int zeroBasedStart = start - 1;
        int zeroBasedEnd = end - 1;
        return service.getPaginated(zeroBasedStart, zeroBasedEnd)
            .onItem().transform(result -> Response.ok(result).build());
    }

    /*
     * Only for testing purposes - Cleans up all products - Tried calling delete operation from test cases but was 
     * facing issues related to vert.x context not being propagated properly. Thus created this endpoint to facilitate cleanup. after tests.
     * Not to be used in production. Only added to ease the testing process for assignment.
     */
    @POST
    @Path("/test/cleanup")
    public void cleanup() {
    	service.deletAllProducts();
    	
    }
}
