package com.vitor.dscatalog.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    /*Dado de auditoria para saber o instante em que esse registro foi criado*/
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;
    /*Dado de auditoria para saber o instante em que esse registro foi alterado*/
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    /*NÃO criados os Sets desses dados de auditoria, pois serão gerados AUTOMATICAMENTE pelos métodos abaixo ao criar e alterar os dados respectivamente*/
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /*@PrePersist é uma anotação padrão que será executada AUTOMATICAMENTE ao executar um SAVE do JPA pela PRIMEIRA VEZ
     * ex: category.save()*/
    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    /*O mesmo que o acima, porém para um SAVE que NÃO é a PRIMEIRA VEZ*/
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
