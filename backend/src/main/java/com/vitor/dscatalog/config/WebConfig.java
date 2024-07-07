package com.vitor.dscatalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/*Classe de configuração para garantir uma estrutura JSON estável e sumir o aviso de que não há garantia da estabilidade do resultado do JSON para resultados paginados*/
/*https://docs.spring.io/spring-data/data-commons/docs/3.3.0-M1/api/org/springframework/data/web/config/EnableSpringDataWebSupport.PageSerializationMode.html*/
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
}