package com.alfonsovidrio.springboot.app.springboot_crud.entities;

import com.alfonsovidrio.springboot.app.springboot_crud.validation.IsExistsDb;
import com.alfonsovidrio.springboot.app.springboot_crud.validation.IsRequired;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @IsExistsDb
    @IsRequired(message = "{IsRequired.product.code}")
    private String code;

    @NotBlank(message = "{NotBlank.product.name}")
    @Size(min = 3, max = 50, message = "{Size.product.name}")
    private String name;

    @NotNull(message = "{NotNull.product.price}")
    @Min(value = 10, message = "{Min.product.price}")
    private Double price;

    @IsRequired(message = "{IsRequired.product.description}")
    @Size(min = 10, max = 255, message = "{Size.product.description}")
    private String description;
    
    public Product() {
    }

    public Product(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    
}
