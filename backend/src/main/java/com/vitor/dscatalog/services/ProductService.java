package com.vitor.dscatalog.services;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.entities.Product;
import com.vitor.dscatalog.repositories.ProductRepository;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> result = repository.findAll(pageable);
        return result.map(ProductDTO::new);
    }
}
