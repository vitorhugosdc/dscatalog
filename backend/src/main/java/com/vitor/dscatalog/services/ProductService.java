package com.vitor.dscatalog.services;

import com.vitor.dscatalog.dto.CategoryDTO;
import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.entities.Product;
import com.vitor.dscatalog.projections.ProductCategoriesProjection;
import com.vitor.dscatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> result = repository.findAll(pageable);
        return result.map(ProductDTO::new);
    }

    /*Método para retornar os produtos já com as categorias
     * No caso, para fazer projeções dentro de projeções (por ex, ter uma projeção de CategoryProjection dentro de um ProductProjection
     * deveria-se usar consultas JPQL https://stackoverflow.com/questions/48995744/java-spring-projection-inside-projection
     * A solução então foi criar o ProductCategoriesProjection com os dados de Category lá mesmo, sem representar um objeto e
     * instanciar tudo aqui nesse método utilizando o map para instanciar os produtos e adicionar as instâncias das categorias
     * retornando um PageImpl, que recebe a lista de ProductDTO, o pageable e o total de elementos do countQuery*/

    /* Por algum motivo ainda não identificado, funciona o size e o page, mas sort NÃO nos RequestParams
     * Identifiquei também que, isso só acontece quando há junção de tabelas com JOIN, onde size=12, page=0, por exemplo, funciona, mas sort=name ou qualquer outra coisa, não
     * Aqui um artigo confirmando que: não da para ordernar consultas nativas sem ser MANUALMENTE inserindo o ORDER BY, PORÉM, eu notei que não da para ordernar CONSULTAS NATIVAS COM JOIN, pois tem consultas nativas que ordenam sim, mas que operam somente em uma tabela
     * https://thorben-janssen.com/native-queries-with-spring-data-jpa/*/
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsWithCategories(Pageable pageable) {
        Page<ProductCategoriesProjection> projections = repository.searchProductsWithCategories(pageable);

        Map<Long, ProductDTO> productMap = new HashMap<>();

        for (ProductCategoriesProjection projection : projections) {
            ProductDTO productDTO = productMap.get(projection.getId());
            if (productDTO == null) {
                productDTO = new ProductDTO(
                        projection.getId(),
                        projection.getName(),
                        projection.getDescription(),
                        projection.getPrice(),
                        projection.getImgUrl()
                );
                productMap.put(projection.getId(), productDTO);
            }
            productDTO.getCategories().add(new CategoryDTO(projection.getCategoryId(), projection.getCategoryName()));
        }
        List<ProductDTO> productDTOList = new ArrayList<>(productMap.values());
        return new PageImpl<>(productDTOList, pageable, projections.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsWithCategoriesJPQL(Pageable pageable) {
        Page<Product> result = repository.searchProductsWithCategoriesJPQL(pageable);
        return result.map(ProductDTO::new);
    }
}
