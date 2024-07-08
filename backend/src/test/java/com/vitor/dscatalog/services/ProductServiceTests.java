package com.vitor.dscatalog.services;

import com.vitor.dscatalog.repositories.ProductRepository;
import com.vitor.dscatalog.services.exceptions.DatabaseException;
import com.vitor.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)*/
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    /*O serviço a ser testado
     * Importante: o teste de UNIDADE não tem acesso ao repositório real, então temos que pensar em como testar o service
     * SEM depender do repositorio*/
    @InjectMocks
    private ProductService service;

    /*Não instanciamos a dependência, simulamos o comportamento dela com um Mock
     * Quando usamos o @ExtendWith(SpringExtension.class), utilizamos o @Mock
     * Usar quando a classe de teste não carrega o contexto da aplicação. É mais rápido e enxuto. @ExtendWith
     * Quando criamos um Mock, DEVEMOS configurar o comportamento simulado dele*/
    @Mock
    private ProductRepository repository;

    /*
    Usar @MockBean quando a classe de teste carrega o contexto da aplicação e precisa mockar algum bean do sistema.
    @WebMvcTest
    @SpringBootTest
    -----
    @MockBean
    private ProductRepository repository;*/

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        /*Lembrando que esses números, por ser um teste de UNIDADE no SERVICE, não tem nada a ver com os dados do banco de dados
         * Poderiam ser qualquer valores, desde que sejam diferentes, por ex: 1L, 2L, 3L*/
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;

        /*CONFIGURAÇÃO DO NOSSO MOCK DO REPOSITORY*/
        /*Não é necessário colocar Mockito.algumMetodo()
         * Da pra fazer import estático e usar doNothing() por ex direto, mas o nélio usa Mockito. para ensinar*/

        /*when é usado quando é um método que retorna alguma coisa
         * Basicamente o método abaixo define o que deve acontecer quando for chamado existsById passando um id válido, no caso, retornar true*/
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        /*Basicamente: quando eu chamar o deleteById com um id existente, esse método não vai fazer nada, oq é o esperado*/
        Mockito.doNothing().when(repository).deleteById(existingId);
        /*Por que DataIntegrityViolationException ao invés de DatabaseException? pois no meu service, o REPOSITORY (dependencia) lança DataIntegrityViolationException
           Então, temos que simular o comportamento desse repository lançando DataIntegrityViolationException
           pois lançar DatabaseException é do SERVICE, que é oq estamos testando*/
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        /*Perceba que para o método delete, ele tem 2 métodos da dependência, o existsById e o deleteBYid do ProductRepository.
         * Então tivemos que configurar o comportamento simulado de cada um deles*/
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        /*Essa chamada verifica se o método deleteById foi chamado no teste acima*/
        Mockito.verify(repository).deleteById(existingId);

        /* o Mockito tem várias sobrecargas, uma delas é a abaixo, Mockito.times(1) verifica se o deleteById foi chamado só uma vez
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);*/
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
