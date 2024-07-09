package com.vitor.dscatalog.services;

import com.vitor.dscatalog.dto.CategoryDTO;
import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.entities.Category;
import com.vitor.dscatalog.entities.Product;
import com.vitor.dscatalog.projections.ProductCategoriesProjection;
import com.vitor.dscatalog.repositories.CategoryRepository;
import com.vitor.dscatalog.repositories.ProductRepository;
import com.vitor.dscatalog.services.exceptions.DatabaseException;
import com.vitor.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

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
     * https://thorben-janssen.com/native-queries-with-spring-data-jpa/
     *
     * Infelizmente notei que está incorreto o método, ele retorna todos, mas ao ir para a ULTIMA página da algum erro de contagem
     * Aparentemente esses métodos funcionavam, mas ele retorna o numero certo de elementos só até a ultima pagina, se eu passar como parametro o ?page=x por ex, e esse x for a ultima página, ele da 1 a mais, tem 25 no banco de dados e da 26
     * analisando, eu apenas acho que ele tá contando o numero de categorias, pois todos produtos do seed tem 1 só categoria, com exceção do primeiro, que possui 2
     * eu usei o distinct pois sem ele retornava 26 produtos, pois 1 retornava 2x devido a ter 2 categorias
     * com o distinct parecia que havia consertado isso, ao invés de contar 26, contava 25 corretamente mas especificamente na ultima pagina da 26, não sei porque
     * tentei adicionar um novo produto e percebi na ultima pagina que os 26 que era pra aparecer, agora são 28, pois adicionei 1 produto com 2 categorias, então bate ele contanto as categorias
     * mas na consulta q eu enviei o distinct tá no product_id, então não sei
     *
     * Talvez usar um Pageof. com PageRequest ao invés do Pageable funcione? tentar depois*/
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

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        try {
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setImgUrl(dto.getImgUrl());
            product.setDate(dto.getDate());
            for (CategoryDTO cat : dto.getCategories()) {
                Category category = categoryRepository.getReferenceById(cat.getId());
                product.getCategories().add(category);
            }
            product = repository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product product = repository.getReferenceById(id);
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setImgUrl(dto.getImgUrl());

            product.getCategories().clear();

            for (CategoryDTO cat : dto.getCategories()) {
                Category category = categoryRepository.getReferenceById(cat.getId());
                product.getCategories().add(category);
               /*Também funcionaria, talvez até teria certeza de ter a categoria se por ex, fosse selecionada no front-end corretamente
               em algum campo que só mostra categorias existentes
                product.getCategories().add(new Category(cat.getId(), null));*/
            }
            product = repository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referencial Integrity failure");
        }
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new ProductDTO(product);
    }
}
