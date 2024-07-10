package com.vitor.dscatalog.controllers;

import com.vitor.dscatalog.dto.ProductDTO;
import com.vitor.dscatalog.services.ProductService;
import com.vitor.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() throws Exception {

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        Mockito.when(service.findAll(ArgumentMatchers.any())).thenReturn(page);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {

        /*perform faz uma requisição*/
        mockMvc.perform(get("/products")).andExpect(status().isOk());

    }

}
