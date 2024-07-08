package com.vitor.dscatalog.tests;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.entities.Category;
import com.vitor.dscatalog.entities.Product;

import java.time.LocalDate;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", LocalDate.parse("2020-05-04"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        return new ProductDTO(createProduct());
    }
}
