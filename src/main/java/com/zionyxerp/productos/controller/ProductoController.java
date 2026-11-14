package com.zionyxerp.productos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zionyxerp.productos.dto.ProductResponse;
import com.zionyxerp.productos.services.ProductService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor 
public class ProductoController {
    private final ProductService productService;

    @GetMapping 
    public ResponseEntity<List<ProductResponse>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

}
