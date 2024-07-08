package com.vitor.dscatalog.repositories;

import com.vitor.dscatalog.entities.Product;
import com.vitor.dscatalog.projections.ProductCategoriesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true,
    value = "SELECT tb_product.id, tb_product.name, tb_product.description, tb_product.price, tb_product.img_url, tb_category.id AS categoryId, tb_category.name AS categoryName " +
            "FROM tb_product " +
            "INNER JOIN tb_product_category " +
            "ON tb_product.id = tb_product_category.product_id " +
            "INNER JOIN tb_category " +
            "ON tb_product_category.category_id = tb_category.id",
    /*DISTINCT pois tem 1 produto com 2 categorias, ai ao invés de dar 25 produtos inseridos, dá 26, pois contou o mesmo produto 2 vezes devido a ter 2 categorias
    * Se tivesse mais produtos com mais de 1 categoria daria uma diferença bem maior
    * Então estamos fazendo a contagem distinta do total de produtos, o que com essa seed, é 25*/
    countQuery = "SELECT COUNT(DISTINCT tb_product.id) from tb_product " +
            "INNER JOIN tb_product_category " +
            "ON tb_product.id = tb_product_category.product_id " +
            "INNER JOIN tb_category " +
            "ON tb_product_category.category_id = tb_category.id")
    Page<ProductCategoriesProjection> searchProductsWithCategories(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories")
    Page<Product> searchProductsWithCategoriesJPQL(Pageable pageable);
}
