package com.vitor.dscatalog.projections;

import java.util.List;

public interface ProductCategoriesProjection {

    Long getId();
    String getName();
    String getDescription();
    Double getPrice();
    String getImgUrl();
    Long getCategoryId();
    String getCategoryName();

    /*Em consultas JPQL, projeções dentro de projeções funciona, então a projeção abaixo funcionaria
    https://stackoverflow.com/questions/48995744/java-spring-projection-inside-projection
    * Como não pretendo usar a JPQL, a melhor maneira de fazer isso foi como foi feito em
    searchProductsWithCategories no ProductService
    * */
    List<CategoryProjection> getCategories();
}
