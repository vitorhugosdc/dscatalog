package com.vitor.dscatalog.repositories;

import com.vitor.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
