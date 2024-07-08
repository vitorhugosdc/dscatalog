/*Mesmo pacote do ProductRepository original*/
package com.vitor.dscatalog.repositories;

import com.vitor.dscatalog.entities.Product;
import com.vitor.dscatalog.services.exceptions.ResourceNotFoundException;
import com.vitor.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    /*Estamos testando o repository DE VERDADE (no caso, o que está no seed)
     * Ele carrega o JPA, os repositories e o banco de dados H2*/
    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistentId;
    private Long nullId;
    private long countTotalProducts;

    /*Esse método é executado antes de cada teste*/
    @BeforeEach
    void setUp() throws Exception{
       existingId = 1L;
       nonExistentId = 1000L;
       nullId = null;
       countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        /*Importante: cada teste faz 1 coisa só, não coloque ifs e complexidade no teste*/

        /*Padrão AAA: Action: execute as ações necessárias*/
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);

        /*Padrão AAA: Assert: declare o que deveria acontecer (resultado esperado)*/
        Assertions.assertFalse(result.isPresent());
    }

    /* Por que esse teste não funcionaria aqui? pois estamos testando o REPOSITORY @DataJpaTest e não o service que lança a exceção personalizada
    quando Id não existe
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists(){
        Peculiaridade de testar exceções, eu coloco basicamente o teste em uma expressão lambda
         assertThrows expera a exceção com o .class (objeto dela) e um executável, que é uma operação
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            long nonExistentId = 100L;
            repository.deleteById(nonExistentId);
        });
    }*/

    @Test
    public void insertShouldPersistWithAutoincrementWhenIdIsNull(){

        Product product = Factory.createProduct();

        /*Algo que ainda não havia notado é que o repository.save insere um novo dado se a entidade a ser salva não possuir id
        * Se ela possuir id, será então um update
        * Por isso no endpoint de insert a gente passa um DTO sem id, e no update a gente passa o id e procura a entidade antes*/;
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1 ,product.getId());
    }
}
