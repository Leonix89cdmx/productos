package com.zionyxerp.productos.services;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.zionyxerp.productos.dto.ProductResponse;
import com.zionyxerp.productos.exception.ProductNotFoundException;

@Service 
public class ProductService {
    
    private final List<ProductResponse> productos = List.of(
        new ProductResponse(
            1L,"Laptop","Laptop Empresarial",
            new BigDecimal("21000.00"),true
        ),
        new ProductResponse(
            2L,"Mouse","Mouse inalambrico",
            new BigDecimal("800.00"),true
        ),
        new ProductResponse(
            3L,"TV 45\" in","Digital Television",
            new BigDecimal("21000.00"),true
        ),
        new ProductResponse(
            4L,"Keyboard","Mechanical Keyboard",
            new BigDecimal("1200.00"),true
        )
    );

    public List<ProductResponse> findAll(){
        return productos;
    }

    public ProductResponse findById(Long pi_id){
        return productos.stream()
        .filter(productos -> productos.id().equals(pi_id))
        .findFirst()
        .orElseThrow(()->new ProductNotFoundException(pi_id));
    }
}
