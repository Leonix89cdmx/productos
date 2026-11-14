package com.zionyxerp.productos.exception;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long id) {
        super("Producto no encontrado con id: " + id);
    }
    
}
