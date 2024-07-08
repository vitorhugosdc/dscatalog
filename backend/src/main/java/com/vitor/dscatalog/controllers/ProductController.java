package com.vitor.dscatalog.controllers;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) {
        //long startTime = System.nanoTime();
        Page<ProductDTO> result = service.findAll(pageable);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1_000_000;
        //System.out.println("findAll duration: " + duration + " ms");
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<Page<ProductDTO>> findAllWithCategories(Pageable pageable){
        //long startTime = System.nanoTime();
        Page<ProductDTO> result = service.searchProductsWithCategories(pageable);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1_000_000;
        //System.out.println("findAll duration: " + duration + " ms");
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/findAllJPQL")
    public ResponseEntity<Page<ProductDTO>> findAllWithCategoriesJPQL(Pageable pageable){
        //long startTime = System.nanoTime();
        Page<ProductDTO> result = service.searchProductsWithCategoriesJPQL(pageable);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1_000_000;
        //System.out.println("findAll duration: " + duration + " ms");
        return ResponseEntity.ok(result);
    }
}
