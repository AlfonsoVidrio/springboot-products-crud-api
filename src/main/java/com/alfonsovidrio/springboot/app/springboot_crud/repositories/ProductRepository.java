package com.alfonsovidrio.springboot.app.springboot_crud.repositories;

import org.springframework.data.repository.CrudRepository;

import com.alfonsovidrio.springboot.app.springboot_crud.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{
    boolean existsByCode(String code);
}
