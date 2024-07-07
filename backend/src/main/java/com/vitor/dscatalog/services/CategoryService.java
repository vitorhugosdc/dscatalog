package com.vitor.dscatalog.services;

import com.vitor.dscatalog.dto.CategoryDTO;
import com.vitor.dscatalog.entities.Category;
import com.vitor.dscatalog.repositories.CategoryRepository;
import com.vitor.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> result = repository.findAll(pageable);
        return result.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category result = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new CategoryDTO(result);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category category = new Category();
        copyDto(dto, category);
        category = repository.save(category);
        return new CategoryDTO(category);
    }

    private void copyDto(CategoryDTO dto, Category entity) {
        entity.setName(dto.getName());
    }
}
