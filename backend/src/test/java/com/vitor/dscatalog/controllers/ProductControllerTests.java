package com.vitor.dscatalog.controllers;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.services.ProductService;
import com.vitor.dscatalog.services.exceptions.ResourceNotFoundException;
import com.vitor.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /*Como estamos usando o @WebMvcTest, precisa ser @MockBean, não só @Mock*/
    @MockBean
    private ProductService service;

    private ProductDTO productDTO;
    /*PageImpl e não Page normal pois o PageImpl aceita um new, pois é concreta*/
    private PageImpl<ProductDTO> page;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 2L;

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        Mockito.when(service.findAll(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(service.findById(existingId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        /*perform faz uma requisição*/
        mockMvc.perform(get("/products")).andExpect(status().isOk());

        /*Poderia ser assim também, atribuindo em uma variável, ai estamos separando, chamando primeiro a requisição e retornando a resposta,
        depois, a partir do resultado, fazendo as assertions
         o .accept(MediaType.APPLICATION_JSON)) é pra configurar MediaType pra JSON

        ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());*/
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                /*$ (cifrao) acessa o objeto de resposta, o JSON todo de resposta*/
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    /*NotFound pois estamos tratando a exceção ResourceNotFound da camada service e retornando 404*/
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        mockMvc.perform(get("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
