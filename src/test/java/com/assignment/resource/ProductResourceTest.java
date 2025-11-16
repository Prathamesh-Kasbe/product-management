package com.assignment.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.assignment.dto.ProductDTO;
import com.assignment.service.ProductService;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class ProductResourceTest {
	
	@BeforeEach
	public void setup() {
		// Clean up the database before each test
		given().when().delete("/products/test/cleanup");
	}

	@Test
	public void testCreateAndGetProduct() {
		ProductDTO dto = new ProductDTO();
		dto.name = "Test";
		dto.description = "Test Desc";
		dto.price = 100.0;
		dto.quantity = 10;

		given().contentType(ContentType.JSON).body(dto).when().post("/products").then().statusCode(201).body("id",
				notNullValue());

		given().when().get("/products").then().statusCode(200).body("size()", greaterThan(0));
	}

	@Test
	public void testUpdateAndDelete() {
		ProductDTO dto = new ProductDTO();
		dto.name = "ToUpdate";
		dto.description = "Update Desc";
		dto.price = 200.0;
		dto.quantity = 5;

		Integer id = given().contentType(ContentType.JSON).body(dto).when().post("/products").then().extract()
				.path("id");

		dto.name = "Updated";
		given().contentType(ContentType.JSON).body(dto).when().put("/products/" + id).then().statusCode(200)
				.body("name", equalTo("Updated"));

		given().when().delete("/products/" + id).then().statusCode(204);
	}

	@Test
	public void testStockCheck() {
		ProductDTO dto = new ProductDTO();
		dto.name = "StockTest";
		dto.description = "Stock Desc";
		dto.price = 50.0;
		dto.quantity = 20;

		Integer id = given().contentType(ContentType.JSON).body(dto).when().post("/products").then().extract()
				.path("id");

		given().when().get("/products/" + id + "/stock?count=8").then().statusCode(200).body("available", is(true));

	}

	@Test
	public void testGetById() {
		given().when().get("/products/1").then().statusCode(200).body("size()", greaterThan(0));
	}

	@Test
	public void testDeleteNotFound() {
		given().when().delete("/products/" + 99999).then().statusCode(404);
	}

	@Test
	public void testBulkAdd() {
		ProductDTO p1 = new ProductDTO();
		p1.name = "BulkRange1";
		p1.description = "Range product 1";
		p1.price = 11.0;
		p1.quantity = 1;

		ProductDTO p2 = new ProductDTO();
		p2.name = "BulkRange2";
		p2.description = "Range product 2";
		p2.price = 12.0;
		p2.quantity = 2;

		ProductDTO p3 = new ProductDTO();
		p3.name = "BulkRange3";
		p3.description = "Range product 3";
		p3.price = 13.0;
		p3.quantity = 3;

		ProductDTO[] arr = new ProductDTO[] { p1, p2, p3 };

		// Add all three products
		given().contentType(ContentType.JSON).body(arr).when().post("/products/bulk").then().statusCode(201)
				.body("name", hasItems("BulkRange1", "BulkRange2", "BulkRange3"));

		given().when().get("/products/sorted").then().statusCode(200).body("[0].price", lessThanOrEqualTo(12.0f))
				.body("[1].price", lessThanOrEqualTo(12.0f));
	}

	@Test
	public void testFindRange() {
		ProductDTO p1 = new ProductDTO();
		p1.name = "BulkRange1";
		p1.description = "Range product 1";
		p1.price = 11.0;
		p1.quantity = 1;

		ProductDTO p2 = new ProductDTO();
		p2.name = "BulkRange2";
		p2.description = "Range product 2";
		p2.price = 12.0;
		p2.quantity = 2;
		
		ProductDTO[] arr = new ProductDTO[] { p1, p2};

		given().contentType(ContentType.JSON).body(arr).when().post("/products/bulk").then().statusCode(201);
		// Request range 1..2 which should return the first two products
		given().when().get("/products/range?start=1&end=2").then().log().body().statusCode(200)
		//.body("[0].name", equalTo("BulkRange1"))
		//.body("[1].name", equalTo("BulkRange2"))
		.body("products.size()", equalTo(2));
	}
	
	@Test
	public void testInvalidIndexRange() {
	    given()
	        .queryParam("start", 7)
	        .queryParam("end", 5)
	    .when()
	        .get("/products/range")
	    .then()
	        .statusCode(400)
	        .body("error", equalTo("Invalid index range"));
	}
}