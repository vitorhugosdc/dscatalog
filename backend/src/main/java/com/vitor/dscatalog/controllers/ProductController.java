package com.vitor.dscatalog.controllers;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    public ResponseEntity<Page<ProductDTO>> findAllWithCategories(Pageable pageable) {
        //long startTime = System.nanoTime();
        Page<ProductDTO> result = service.searchProductsWithCategories(pageable);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1_000_000;
        //System.out.println("findAll duration: " + duration + " ms");
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/findAllJPQL")
    public ResponseEntity<Page<ProductDTO>> findAllWithCategoriesJPQL(Pageable pageable) {
        //long startTime = System.nanoTime();
        Page<ProductDTO> result = service.searchProductsWithCategoriesJPQL(pageable);
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1_000_000;
        //System.out.println("findAll duration: " + duration + " ms");
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
