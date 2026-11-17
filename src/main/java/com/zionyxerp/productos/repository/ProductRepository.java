package com.zionyxerp.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zionyxerp.productos.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{ }